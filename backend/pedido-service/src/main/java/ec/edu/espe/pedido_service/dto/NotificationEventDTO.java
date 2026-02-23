package ec.edu.espe.pedido_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para eventos de notificación publicados a RabbitMQ
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventDTO {
    
    /**
     * ID único del evento
     */
    private String eventId;
    
    /**
     * Nombre del microservicio que genera el evento
     */
    private String microservice;
    
    /**
     * Acción realizada: CREATED, UPDATED, DELETED, ASSIGNED, etc.
     */
    private String action;
    
    /**
     * Tipo de entidad afectada: PEDIDO, REPARTIDOR, VEHICULO, etc.
     */
    private String entityType;
    
    /**
     * ID de la entidad afectada
     */
    private String entityId;
    
    /**
     * Mensaje descriptivo del evento
     */
    private String message;
    
    /**
     * Timestamp del evento
     */
    private String eventTimestamp;
    
    /**
     * Datos adicionales del evento
     */
    private Map<String, Object> data;
    
    /**
     * Severidad del evento: INFO, WARN, ERROR
     */
    private String severity;
}
