import { Injectable, Logger, OnModuleDestroy, OnModuleInit } from "@nestjs/common";
import { ConfigService } from "@nestjs/config/dist/config.service";
import { Connection, Channel, connect, ConsumeMessage} from "amqplib";
import { NotificationService } from "src/notifications/entity/notification.service";
import { NotificationEvent } from './interfaces/notification-event.interface';
import { NotificationsGateway } from 'src/notifications/notifications.gateway';


@Injectable()
export class RabbitMQService implements OnModuleInit, OnModuleDestroy {

    private readonly logger = new Logger(RabbitMQService.name);
    private connection: Connection;
    private channel: Channel;

    private readonly exchangeName = 'notifications_exchange';
    private readonly queueName = 'notifications_queue';
    private readonly routingKey = 'notifications_routingKey';

    constructor(
        private readonly configService: ConfigService,
        private readonly notificationService: NotificationService,
        private readonly notificationsGateway: NotificationsGateway,
    ) {}

    async onModuleInit() {
        await this.connect();
        await this.setupQueue();
        await this.consumeMessages();
    }

    async onModuleDestroy() {
        await this.closeConnection();
    }

    private async connect(): Promise<void> {
        try {
            const host = this.configService.get('RABBITMQ_HOST');
            const port = this.configService.get('RABBITMQ_PORT');
            const username = this.configService.get('RABBITMQ_USERNAME');
            const password = this.configService.get('RABBITMQ_PASSWORD');

            const conexion = `amqp://${username}:${password}@${host}:${port}`;

            this.connection = await connect(conexion);
            this.channel = await this.connection.createChannel();

            this.logger.log('Connected exitosa a RabbitMQ');
        } catch (error) {
            this.logger.error('Error al conectar a RabbitMQ', error);
            throw error;
        }
    }

        private async setupQueue(): Promise<void> {
            try {
                //Declarar exchange
                await this.channel.assertExchange(this.exchangeName, 'topic', { durable: true });
                //Declarar queue- cola
                await this.channel.assertQueue(this.queueName, { durable: true });

                //Vincular la queue al exchange con una routing key
                await this.channel.bindQueue(this.queueName, this.exchangeName, this.routingKey);

                this.logger.log(`Cola ${this.queueName} correctamente configurada`);
            } catch (error) {
                this.logger.error(`Error al configurar la cola: ${this.queueName}:`, error);
                throw error;
            }
        }

            private async consumeMessages(): Promise<void> {
        try {
        await this.channel.consume(
            this.queueName,
            async (message: ConsumeMessage | null) => {
            if (message) {
                try {
                const contentString = message.content.toString();
                this.logger.debug(`Mensaje recibido: ${contentString}`);

                const content = JSON.parse(contentString) as NotificationEvent;

                this.logger.log(
                    `Nueva notificación recibida: ${content.action} - ${content.entityType}`,
                );
                this.logger.debug(
                    `Timestamp recibido: ${content.eventTimestamp}, tipo: ${typeof content.eventTimestamp}`,
                );

                // Parsear el timestamp correctamente
                let eventTimestamp: Date;
                if (content.eventTimestamp) {
                    try {
                    // Intentar parsear como ISO string
                    eventTimestamp = new Date(content.eventTimestamp);

                    // Si es inválido, usar fecha actual
                    if (isNaN(eventTimestamp.getTime())) {
                        this.logger.warn(
                        `Timestamp inválido: ${content.eventTimestamp}, usando fecha actual`,
                        );
                        eventTimestamp = new Date();
                    }
                    } catch (error) {
                    this.logger.warn(
                        `Error parseando timestamp: ${error}, usando fecha actual`,
                    );
                    eventTimestamp = new Date();
                    }
                } else {
                    this.logger.warn(
                    `Timestamp no proporcionado, usando fecha actual`,
                    );
                    eventTimestamp = new Date();
                }

                // Guardar en base de datos
                await this.notificationService.create({
                    eventId: content.eventId,
                    microservice: content.microservice,
                    action: content.action,
                    entityType: content.entityType,
                    entityId: content.entityId,
                    message: content.message,
                    eventTimestamp: eventTimestamp.toISOString(),
                    data: content.data || {},
                    severity: content.severity || 'INFO',
                });

                // Broadcast vía WebSocket a clientes suscritos
                this.broadcastToWebSocket(content);

                // Confirmar recepción del mensaje
                this.channel.ack(message);
                } catch (error) {
                this.logger.error('Error procesando mensaje:', error);
                this.logger.error(
                    'Contenido del mensaje:',
                    message.content.toString(),
                );

                // Rechazar mensaje (no lo reintenta)
                this.channel.nack(message, false, false);
                }
            }
            },
            {
            noAck: false, // Confirmación manual
            },
        );

        this.logger.log(`Consumiendo mensajes de la cola: ${this.queueName}`);
        } catch (error) {
        this.logger.error('Error iniciando consumo de mensajes:', error);
        throw error;
        }
    }

        private async closeConnection(): Promise<void> {
            try {
                if (this.channel) {
                    await this.channel.close();
                    this.logger.log('Canal de RabbitMQ cerrado');
                }
                if (this.connection) {
                    await this.connection.close();
                    this.logger.log('Conexión a RabbitMQ cerrada');
                }
            } catch (error) {
                this.logger.error('Error al cerrar la conexión a RabbitMQ', error);
            }
    }

    /**
     * Reenvía eventos a clientes WebSocket suscritos
     * Solo usa eventos específicos (pedido:updated, repartidor:updated)
     * que van tanto al topic dedicado como a 'all' (supervisores)
     */
    private broadcastToWebSocket(event: NotificationEvent): void {
        try {
            // Broadcast específico según tipo de entidad
            // Los eventos van a: topic específico + 'all' (para supervisores)
            if (event.entityType === 'PEDIDO') {
                this.notificationsGateway.broadcastPedidoUpdate(event.entityId, {
                    action: event.action,
                    message: event.message,
                    data: event.data,
                });
            } else if (event.entityType === 'REPARTIDOR') {
                this.notificationsGateway.broadcastRepartidorUpdate(event.entityId, {
                    action: event.action,
                    message: event.message,
                    data: event.data,
                });
            }

            this.logger.log(
                `Evento broadcast vía WebSocket: ${event.entityType} - ${event.action}`,
            );
        } catch (error) {
            this.logger.error('Error broadcasting a WebSocket:', error);
        }
    }
}