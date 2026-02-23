package ec.edu.espe.fleet_service.service;

import ec.edu.espe.fleet_service.config.RabbitMQConfig;
import ec.edu.espe.fleet_service.dto.NotificationEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio productor de notificaciones a RabbitMQ para fleet-service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;
    
    private static final String MICROSERVICE_NAME = "fleet-service";
    private static final String SEVERITY_INFO = "INFO";
    private static final String SEVERITY_WARN = "WARN";
    
    /**
     * Publica un evento genérico de notificación
     */
    public void publishEvent(NotificationEventDTO event) {
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATIONS_EXCHANGE,
                RabbitMQConfig.NOTIFICATIONS_ROUTING_KEY,
                event
            );
            log.info("Evento publicado: {} - {} [{}]", 
                event.getAction(), event.getEntityType(), event.getEntityId());
        } catch (Exception e) {
            log.error("Error al publicar evento a RabbitMQ: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Publica evento de repartidor asignado
     */
    public void publishRepartidorAsignado(String repartidorId, String pedidoId, Map<String, Object> additionalData) {
        Map<String, Object> data = new HashMap<>();
        data.put("pedidoId", pedidoId);
        if (additionalData != null) {
            data.putAll(additionalData);
        }
        
        NotificationEventDTO event = NotificationEventDTO.builder()
            .eventId(UUID.randomUUID().toString())
            .microservice(MICROSERVICE_NAME)
            .action("ASSIGNED")
            .entityType("REPARTIDOR")
            .entityId(repartidorId)
            .message(String.format("Repartidor asignado al pedido %s", pedidoId))
            .eventTimestamp(LocalDateTime.now().toString())
            .severity(SEVERITY_INFO)
            .data(data)
            .build();
        
        publishEvent(event);
    }
    
    /**
     * Publica evento de estado de repartidor actualizado
     */
    public void publishRepartidorEstadoActualizado(String repartidorId, String estadoAnterior, String estadoNuevo, Map<String, Object> additionalData) {
        Map<String, Object> data = new HashMap<>();
        data.put("estadoAnterior", estadoAnterior);
        data.put("estadoNuevo", estadoNuevo);
        if (additionalData != null) {
            data.putAll(additionalData);
        }
        
        NotificationEventDTO event = NotificationEventDTO.builder()
            .eventId(UUID.randomUUID().toString())
            .microservice(MICROSERVICE_NAME)
            .action("UPDATED")
            .entityType("REPARTIDOR")
            .entityId(repartidorId)
            .message(String.format("Estado del repartidor actualizado: %s → %s", estadoAnterior, estadoNuevo))
            .eventTimestamp(LocalDateTime.now().toString())
            .severity(SEVERITY_INFO)
            .data(data)
            .build();
        
        publishEvent(event);
    }
    
    /**
     * Publica evento de repartidor creado
     */
    public void publishRepartidorCreado(String repartidorId, Map<String, Object> additionalData) {
        Map<String, Object> data = new HashMap<>();
        if (additionalData != null) {
            data.putAll(additionalData);
        }
        
        NotificationEventDTO event = NotificationEventDTO.builder()
            .eventId(UUID.randomUUID().toString())
            .microservice(MICROSERVICE_NAME)
            .action("CREATED")
            .entityType("REPARTIDOR")
            .entityId(repartidorId)
            .message("Nuevo repartidor registrado en el sistema")
            .eventTimestamp(LocalDateTime.now().toString())
            .severity(SEVERITY_INFO)
            .data(data)
            .build();
        
        publishEvent(event);
    }
    
    /**
     * Publica evento de vehículo actualizado
     */
    public void publishVehiculoEstadoActualizado(String vehiculoId, String estadoAnterior, String estadoNuevo) {
        Map<String, Object> data = new HashMap<>();
        data.put("estadoAnterior", estadoAnterior);
        data.put("estadoNuevo", estadoNuevo);
        
        NotificationEventDTO event = NotificationEventDTO.builder()
            .eventId(UUID.randomUUID().toString())
            .microservice(MICROSERVICE_NAME)
            .action("UPDATED")
            .entityType("VEHICULO")
            .entityId(vehiculoId)
            .message(String.format("Estado del vehículo actualizado: %s → %s", estadoAnterior, estadoNuevo))
            .eventTimestamp(LocalDateTime.now().toString())
            .severity(SEVERITY_INFO)
            .data(data)
            .build();
        
        publishEvent(event);
    }
}
