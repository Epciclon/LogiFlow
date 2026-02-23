package ec.edu.espe.pedido_service.service;

import ec.edu.espe.pedido_service.config.RabbitMQConfig;
import ec.edu.espe.pedido_service.dto.NotificationEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio productor de notificaciones a RabbitMQ
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;
    
    private static final String MICROSERVICE_NAME = "pedido-service";
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
     * Publica evento de pedido creado
     */
    public void publishPedidoCreado(String pedidoId, Map<String, Object> additionalData) {
        Map<String, Object> data = new HashMap<>();
        if (additionalData != null) {
            data.putAll(additionalData);
        }
        
        NotificationEventDTO event = NotificationEventDTO.builder()
            .eventId(UUID.randomUUID().toString())
            .microservice(MICROSERVICE_NAME)
            .action("CREATED")
            .entityType("PEDIDO")
            .entityId(pedidoId)
            .message("Nuevo pedido creado exitosamente")
            .eventTimestamp(LocalDateTime.now().toString())
            .severity(SEVERITY_INFO)
            .data(data)
            .build();
        
        publishEvent(event);
    }
    
    /**
     * Publica evento de pedido actualizado
     */
    public void publishPedidoEstadoActualizado(String pedidoId, String estadoAnterior, String estadoNuevo, Map<String, Object> additionalData) {
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
            .entityType("PEDIDO")
            .entityId(pedidoId)
            .message(String.format("Estado del pedido actualizado: %s → %s", estadoAnterior, estadoNuevo))
            .eventTimestamp(LocalDateTime.now().toString())
            .severity(SEVERITY_INFO)
            .data(data)
            .build();
        
        publishEvent(event);
    }
    
    /**
     * Publica evento de pedido cancelado
     */
    public void publishPedidoCancelado(String pedidoId, String motivo) {
        Map<String, Object> data = new HashMap<>();
        data.put("motivo", motivo);
        
        NotificationEventDTO event = NotificationEventDTO.builder()
            .eventId(UUID.randomUUID().toString())
            .microservice(MICROSERVICE_NAME)
            .action("CANCELLED")
            .entityType("PEDIDO")
            .entityId(pedidoId)
            .message("Pedido cancelado: " + motivo)
            .eventTimestamp(LocalDateTime.now().toString())
            .severity(SEVERITY_WARN)
            .data(data)
            .build();
        
        publishEvent(event);
    }
}
