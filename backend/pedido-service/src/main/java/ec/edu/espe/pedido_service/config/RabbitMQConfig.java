package ec.edu.espe.pedido_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el servicio de pedidos
 * Permite publicar eventos de creación y actualización de pedidos
 */
@Configuration
public class RabbitMQConfig {

    // Exchange principal de notificaciones
    public static final String NOTIFICATIONS_EXCHANGE = "notifications_exchange";
    
    // Queue para notificaciones
    public static final String NOTIFICATIONS_QUEUE = "notifications_queue";
    
    // Routing key para notificaciones
    public static final String NOTIFICATIONS_ROUTING_KEY = "notifications_routingKey";

    /**
     * Declara el exchange de tipo topic para notificaciones
     */
    @Bean
    public TopicExchange notificationsExchange() {
        return new TopicExchange(NOTIFICATIONS_EXCHANGE, true, false);
    }

    /**
     * Declara la cola de notificaciones
     */
    @Bean
    public Queue notificationsQueue() {
        return new Queue(NOTIFICATIONS_QUEUE, true);
    }

    /**
     * Vincula la cola con el exchange usando la routing key
     */
    @Bean
    public Binding notificationsBinding(Queue notificationsQueue, TopicExchange notificationsExchange) {
        return BindingBuilder
                .bind(notificationsQueue)
                .to(notificationsExchange)
                .with(NOTIFICATIONS_ROUTING_KEY);
    }

    /**
     * Conversor de mensajes a JSON
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Template de RabbitMQ configurado con el conversor JSON
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
