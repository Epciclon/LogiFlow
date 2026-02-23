import axios from 'axios';
import dotenv from 'dotenv';

dotenv.config();

// Usar Kong Gateway como punto único de entrada (puerto 8000)
// Kong config: strip_path = true elimina el prefijo /pedidos y /fleet
// Por eso la base debe incluir /api para que llegue correctamente al backend
const PEDIDO_SERVICE_URL = process.env.PEDIDO_SERVICE_URL || 'http://kong:8000/pedidos/api/pedidos';
const FLEET_SERVICE_URL = process.env.FLEET_SERVICE_URL || 'http://kong:8000/fleet/api/repartidores';
const BILLING_SERVICE_URL = process.env.BILLING_SERVICE_URL || 'http://kong:8000/billing/api/facturas';

export class DeliveryDataSource {
  
  // Helper para crear headers con JWT
  private static getAuthHeaders(token?: string) {
    return token ? { Authorization: `Bearer ${token}` } : {};
  }

  // Helper para manejar errores HTTP
  private static handleError(error: any, resourceType: string, id?: string) {
    if (error.response) {
      const status = error.response.status;
      const message = error.response.data?.message || error.message;
      
      switch (status) {
        case 404:
          throw new Error(`${resourceType}${id ? ` con ID '${id}'` : ''} no encontrado`);
        case 403:
          throw new Error(`No tienes permisos para acceder a ${resourceType}`);
        case 401:
          throw new Error('Token de autenticación inválido o expirado');
        case 500:
          throw new Error(`Error del servidor al obtener ${resourceType}: ${message}`);
        default:
          throw new Error(`Error al obtener ${resourceType}: ${message}`);
      }
    }
    throw new Error(`Error de conexión al obtener ${resourceType}: ${error.message}`);
  }
  
  // --- Pedidos ---
  
  static async getPedidoById(id: string, token?: string) {
    try {
      const response = await axios.get(`${PEDIDO_SERVICE_URL}/${id}`, {
        headers: this.getAuthHeaders(token)
      });
      return response.data;
    } catch (error) {
      this.handleError(error, 'Pedido', id);
    }
  }

  static async getPedidos(filtro: any, token?: string) {
    try {
      const params = new URLSearchParams();
      if (filtro?.estado) params.append('estado', filtro.estado);
      if (filtro?.zonaId) params.append('zonaId', filtro.zonaId);
      if (filtro?.repartidorId) params.append('repartidorId', filtro.repartidorId);
      // NOTA: El backend NO soporta filtro por fecha via query params
      // Debemos filtrar en el cliente si se pasa filtro.fecha

      const response = await axios.get(`${PEDIDO_SERVICE_URL}`, {
        params,
        headers: this.getAuthHeaders(token)
      });
      
      let pedidos = Array.isArray(response.data) ? response.data : [];
      
      // Filtrar por fecha en el cliente si se especificó
      if (filtro?.fecha) {
        pedidos = pedidos.filter((p: any) => {
          if (!p.fechaCreacion) return false;
          // Manejar tanto 'T' como espacio en timestamp
          const fechaPedido = p.fechaCreacion.split(/[T ]/)[0]; // Obtener solo YYYY-MM-DD
          return fechaPedido === filtro.fecha;
        });
      }
      
      return pedidos;
    } catch (error) {
      this.handleError(error, 'Pedidos');
    }
  }

  // --- Flota ---

  static async getRepartidorById(id: string, token?: string) {
    if (!id) return null;
    try {
      const response = await axios.get(`${FLEET_SERVICE_URL}/${id}`, {
        headers: this.getAuthHeaders(token)
      });
      return response.data;
    } catch (error) {
      this.handleError(error, 'Repartidor', id);
    }
  }

  static async getFlotaResumen(zonaId: string, token?: string) {
    try {
        // Obtener todos los repartidores activos
        const response = await axios.get(FLEET_SERVICE_URL, {
          headers: this.getAuthHeaders(token)
        });
        
        const repartidores = Array.isArray(response.data) ? response.data : [];
        
        // Filtrar INACTIVOS - solo contar repartidores en flota activa
        const flotaActiva = repartidores.filter((r: any) => r.estado !== 'INACTIVO');
        const total = flotaActiva.length;
        
        // Contar por estado (sin incluir INACTIVOS)
        const disponibles = flotaActiva.filter((r: any) => r.estado === 'DISPONIBLE').length;
        const enRuta = flotaActiva.filter((r: any) => r.estado === 'EN_RUTA').length;
        const fueraDeServicio = flotaActiva.filter((r: any) => 
          r.estado === 'DESCANSO' || r.estado === 'MANTENIMIENTO'
        ).length;
        
        return { total, disponibles, enRuta, fueraDeServicio };
    } catch (error) {
        console.error('Error al obtener stats de flota:', error);
        throw new Error('No se pudo obtener información de la flota');
    }
  }

  // --- Billing / Facturas ---

  static async getFacturaByPedidoId(pedidoId: string, token?: string) {
    try {
      const response = await axios.get(`${BILLING_SERVICE_URL}/pedido/${pedidoId}`, {
        headers: this.getAuthHeaders(token)
      });
      return response.data;
    } catch (error: any) {
      // 404 es normal si el pedido no tiene factura aún
      if (error.response?.status === 404) {
        return null;
      }
      this.handleError(error, 'Factura', `pedido ${pedidoId}`);
    }
  }

  static async getFacturaById(id: string, token?: string) {
    try {
      const response = await axios.get(`${BILLING_SERVICE_URL}/${id}`, {
        headers: this.getAuthHeaders(token)
      });
      return response.data;
    } catch (error) {
      this.handleError(error, 'Factura', id);
    }
  }

  // --- KPIs y Reportes ---
  
  static async getKPIDiario(fecha: string, zonaId?: string, token?: string) {
    try {
      // TODO: Implementar endpoint real en los microservicios para KPIs
      // Por ahora, calcular desde los datos disponibles
      
      const params = new URLSearchParams();
      params.append('fechaInicio', fecha);
      params.append('fechaFin', fecha);
      if (zonaId) params.append('zonaId', zonaId);

      // Obtener pedidos del día
      const pedidos = await this.getPedidos({ fecha, zonaId }, token);
      
      console.log(`[KPI DEBUG] Fecha: ${fecha}, ZonaId: ${zonaId || 'N/A'}`);
      console.log(`[KPI DEBUG] Pedidos recibidos: ${JSON.stringify(pedidos?.slice(0, 2) || [])}...`);
      console.log(`[KPI DEBUG] Total pedidos: ${pedidos?.length || 0}`);
      
      // Validar que pedidos no sea undefined
      if (!pedidos || !Array.isArray(pedidos)) {
        return {
          fecha,
          totalPedidos: 0,
          ingresosTotales: 0,
          tiempoPromedioEntregaMin: 0,
          entregasPorZona: []
        };
      }
      
      // Excluir pedidos CANCELADOS y DEVUELTOS de los cálculos de KPI
      const pedidosValidos = pedidos.filter((p: any) => 
        p.estado !== 'CANCELADO' && p.estado !== 'DEVUELTO'
      );
      
      // Calcular KPIs desde los datos reales (sin cancelados ni devueltos)
      const totalPedidos = pedidosValidos.length;
      const ingresosTotales = pedidosValidos.reduce((sum: number, p: any) => sum + (p.tarifaTotal || 0), 0);
      
      // Calcular tiempo promedio de entrega (solo pedidos completados, sin cancelados)
      const pedidosCompletados = pedidosValidos.filter((p: any) => p.estado === 'ENTREGADO' && p.fechaEntregaReal && p.fechaCreacion);
      const tiempoPromedioMin = pedidosCompletados.length > 0
        ? pedidosCompletados.reduce((sum: number, p: any) => {
            const inicio = new Date(p.fechaCreacion).getTime();
            const fin = new Date(p.fechaEntregaReal).getTime();
            return sum + ((fin - inicio) / 60000); // Convertir a minutos
          }, 0) / pedidosCompletados.length
        : 0;

      // Agrupar entregas por zona usando el campo zonaId (excluir CANCELADOS y DEVUELTOS)
      const entregasPorZonaMap = new Map<string, number>();
      pedidosValidos.forEach((p: any) => {
        if (p.zonaId) {
          entregasPorZonaMap.set(p.zonaId, (entregasPorZonaMap.get(p.zonaId) || 0) + 1);
        }
      });
      
      const entregasPorZona = Array.from(entregasPorZonaMap.entries()).map(([zonaId, cantidad]) => ({
        zonaId,
        cantidad
      }));
      
      return {
        fecha,
        totalPedidos,
        ingresosTotales: Math.round(ingresosTotales * 100) / 100,
        tiempoPromedioEntregaMin: Math.round(tiempoPromedioMin), // Entero sin decimales
        entregasPorZona
      };
    } catch (error) {
      this.handleError(error, 'KPIs diarios');
    }
  }
}