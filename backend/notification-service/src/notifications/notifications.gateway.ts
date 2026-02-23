import {
  WebSocketGateway,
  WebSocketServer,
  OnGatewayConnection,
  OnGatewayDisconnect,
  SubscribeMessage,
  MessageBody,
  ConnectedSocket,
} from '@nestjs/websockets';
import { Server, Socket } from 'socket.io';
import { Logger, UnauthorizedException } from '@nestjs/common';
import { verify } from 'jsonwebtoken';

/**
 * Interface para eventos cacheados
 */
interface CachedEvent {
  topic: string;
  event: string;
  data: any;
  timestamp: Date;
}

/**
 * Gateway WebSocket para notificaciones en tiempo real
 * Permite a los clientes suscribirse a eventos específicos
 */
@WebSocketGateway({
  cors: {
    origin: '*',
    credentials: true,
  },
})
export class NotificationsGateway
  implements OnGatewayConnection, OnGatewayDisconnect
{
  @WebSocketServer()
  server: Server;

  private readonly logger = new Logger(NotificationsGateway.name);
  private connectedClients = new Map<string, Set<string>>();
  
  // Cache de eventos para replay (últimos 50 eventos)
  private eventCache: CachedEvent[] = [];
  private readonly MAX_CACHED_EVENTS = 50;
  private readonly EVENT_TTL_MS = 5 * 60 * 1000; // 5 minutos
  
  // JWT Secret (en producción debe venir de variable de entorno)
  private readonly JWT_SECRET = process.env.JWT_SECRET || 'logiflow-super-secret-key-2024-change-this-in-production';

  /**
   * Maneja nueva conexión de cliente
   * Valida JWT y envía eventos cacheados (replay)
   */
  handleConnection(client: Socket) {
    try {
      // 1. Validar JWT del handshake
      const token = this.extractToken(client);
      
      if (token) {
        const decoded = this.validateToken(token);
        
        // Guardar info del usuario autenticado en el socket
        (client as any).user = decoded;
        
        this.logger.log(`Cliente autenticado: ${client.id} (user: ${decoded.sub || 'unknown'})`);
      } else {
        this.logger.warn(`Cliente sin token JWT: ${client.id} (modo desarrollo)`);
      }
      
      // 2. Limpiar eventos expirados del cache
      this.cleanExpiredEvents();
      
      // 3. Enviar confirmación de conexión
      client.emit('connected', {
        message: 'Conectado exitosamente al servidor de notificaciones',
        clientId: client.id,
        authenticated: !!token,
        timestamp: new Date().toISOString(),
      });
      
      // Replay se hará cuando el cliente se suscriba a un canal
      
    } catch (error) {
      this.logger.error(`Error en autenticación: ${error.message}`);
      
      // En producción, rechazar la conexión
      if (process.env.NODE_ENV === 'production') {
        client.emit('error', { message: 'Autenticación fallida' });
        client.disconnect();
      } else {
        // En desarrollo, permitir conexión sin JWT
        this.logger.warn('Modo desarrollo: permitiendo conexión sin JWT');
        client.emit('connected', {
          message: 'Conectado en modo desarrollo (sin autenticación)',
          clientId: client.id,
          authenticated: false,
          timestamp: new Date().toISOString(),
        });
      }
    }
  }
  
  /**
   * Extrae el token JWT del handshake
   */
  private extractToken(client: Socket): string | null {
    // Intentar obtener token de query params (ej: ?token=xxx)
    const queryToken = client.handshake.query?.token as string;
    if (queryToken) return queryToken;
    
    // Intentar obtener de headers (Authorization: Bearer xxx)
    const authHeader = client.handshake.headers?.authorization;
    if (authHeader && authHeader.startsWith('Bearer ')) {
      return authHeader.substring(7);
    }
    
    return null;
  }
  
  /**
   * Valida el token JWT
   */
  private validateToken(token: string): any {
    try {
      const decoded = verify(token, this.JWT_SECRET);
      return decoded;
    } catch (error) {
      throw new UnauthorizedException('Token JWT inválido o expirado');
    }
  }
  
  /**
   * Envía eventos recientes al cliente según su suscripción (replay)
   */
  private replayRecentEvents(client: Socket, topic: string) {
    let recentEvents = this.eventCache.slice(-10); // Últimos 10 eventos
    
    // Filtrar eventos según el tópico de suscripción
    if (topic !== 'all') {
      recentEvents = recentEvents.filter(cached => {
        // Si se suscribió a pedido:ID, solo eventos de ese pedido
        if (topic.startsWith('pedido:')) {
          const pedidoId = topic.substring(7);
          return cached.topic === topic || 
                 (cached.data && cached.data.pedidoId === pedidoId);
        }
        // Si se suscribió a repartidor:ID, solo eventos de ese repartidor
        else if (topic.startsWith('repartidor:')) {
          const repartidorId = topic.substring(11);
          return cached.topic === topic || 
                 (cached.data && cached.data.repartidorId === repartidorId);
        }
        // Otros tópicos específicos
        return cached.topic === topic;
      });
    }
    
    if (recentEvents.length > 0) {
      this.logger.log(`Enviando ${recentEvents.length} eventos recientes a ${client.id} (topic: ${topic})`);
      
      recentEvents.forEach(cached => {
        client.emit(cached.event, {
          ...cached.data,
          replayed: true,
          originalTimestamp: cached.timestamp,
        });
      });
      
      client.emit('replay:complete', {
        count: recentEvents.length,
        topic: topic,
        message: `${recentEvents.length} eventos históricos enviados para ${topic}`,
      });
    } else {
      this.logger.log(`No hay eventos recientes para enviar a ${client.id} (topic: ${topic})`);
    }
  }
  
  /**
   * Limpia eventos expirados del cache
   */
  private cleanExpiredEvents() {
    const now = Date.now();
    this.eventCache = this.eventCache.filter(
      event => now - event.timestamp.getTime() < this.EVENT_TTL_MS
    );
  }

  /**
   * Maneja desconexión de cliente
   */
  handleDisconnect(client: Socket) {
    this.logger.log(`Cliente desconectado: ${client.id}`);
    
    // Limpiar suscripciones del cliente
    this.connectedClients.forEach((subscribers, topic) => {
      subscribers.delete(client.id);
      if (subscribers.size === 0) {
        this.connectedClients.delete(topic);
      }
    });
  }

  /**
   * Permite a los clientes suscribirse a tópicos específicos
   * Ejemplos: "pedido:123", "zona:45", "all"
   */
  @SubscribeMessage('subscribe')
  handleSubscribe(
    @MessageBody() data: { topic: string },
    @ConnectedSocket() client: Socket,
  ) {
    const { topic } = data;
    
    if (!topic) {
      client.emit('error', { message: 'Topic requerido' });
      return;
    }

    // Unir cliente al room de Socket.IO
    client.join(topic);
    
    // Registrar suscripción
    if (!this.connectedClients.has(topic)) {
      this.connectedClients.set(topic, new Set());
    }
    const subscribers = this.connectedClients.get(topic);
    if (subscribers) {
      subscribers.add(client.id);
    }

    this.logger.log(`Cliente ${client.id} suscrito a: ${topic}`);
    
    client.emit('subscribed', {
      topic,
      message: `Suscrito exitosamente a ${topic}`,
      timestamp: new Date().toISOString(),
    });
    
    // Enviar replay de eventos recientes según el tópico suscrito
    this.replayRecentEvents(client, topic);
  }

  /**
   * Permite a los clientes desuscribirse de tópicos
   */
  @SubscribeMessage('unsubscribe')
  handleUnsubscribe(
    @MessageBody() data: { topic: string },
    @ConnectedSocket() client: Socket,
  ) {
    const { topic } = data;
    
    if (!topic) {
      client.emit('error', { message: 'Topic requerido' });
      return;
    }

    // Salir del room
    client.leave(topic);
    
    // Eliminar de suscripciones
    const subscribers = this.connectedClients.get(topic);
    if (subscribers) {
      subscribers.delete(client.id);
      if (subscribers.size === 0) {
        this.connectedClients.delete(topic);
      }
    }

    this.logger.log(`Cliente ${client.id} desuscrito de: ${topic}`);
    
    client.emit('unsubscribed', {
      topic,
      message: `Desuscrito de ${topic}`,
      timestamp: new Date().toISOString(),
    });
  }

  /**
   * Broadcast de notificación a todos los clientes suscritos a un tópico
   * Cachea el evento para replay
   */
  broadcastNotification(topic: string, notification: any) {
    this.logger.log(`Broadcasting a topic: ${topic}`);
    
    // Emitir evento
    this.server.to(topic).emit('notification', notification);
    
    // Cachear para replay
    this.cacheEvent(topic, 'notification', notification);
  }
  
  /**
   * Cachea un evento para replay futuro
   */
  private cacheEvent(topic: string, event: string, data: any) {
    this.eventCache.push({
      topic,
      event,
      data,
      timestamp: new Date(),
    });
    
    // Mantener solo los últimos MAX_CACHED_EVENTS
    if (this.eventCache.length > this.MAX_CACHED_EVENTS) {
      this.eventCache.shift();
    }
  }

  /**
   * Broadcast de evento de pedido actualizado
   * - Topic específico: Para clientes que ven su pedido
   * - Topic 'all': Para supervisores que monitorean todo el sistema
   */
  broadcastPedidoUpdate(pedidoId: string, data: any) {
    const topics = [`pedido:${pedidoId}`, 'all'];
    const eventData = {
      pedidoId,
      ...data,
      timestamp: new Date().toISOString(),
    };
    
    topics.forEach(topic => {
      this.server.to(topic).emit('pedido:updated', eventData);
    });
    
    // Cachear para replay
    this.cacheEvent('all', 'pedido:updated', eventData);
    
    this.logger.log(`Pedido ${pedidoId} actualizado broadcast a: ${topics.join(', ')}`);
  }

  /**
   * Broadcast de evento de repartidor actualizado
   * - Topic específico: Para el repartidor en su app móvil
   * - Topic 'all': Para supervisores que monitorean la flota
   */
  broadcastRepartidorUpdate(repartidorId: string, data: any) {
    const topics = [`repartidor:${repartidorId}`, 'all'];
    const eventData = {
      repartidorId,
      ...data,
      timestamp: new Date().toISOString(),
    };
    
    topics.forEach(topic => {
      this.server.to(topic).emit('repartidor:updated', eventData);
    });
    
    // Cachear para replay
    this.cacheEvent('all', 'repartidor:updated', eventData);
    
    this.logger.log(`Repartidor ${repartidorId} actualizado broadcast a: ${topics.join(', ')}`);
  }

  /**
   * Obtener estadísticas de conexiones
   */
  getConnectionStats() {
    const stats = {
      totalClients: this.server.sockets.sockets.size,
      topics: Array.from(this.connectedClients.entries()).map(([topic, clients]) => ({
        topic,
        subscribers: clients.size,
      })),
    };
    
    return stats;
  }
}
