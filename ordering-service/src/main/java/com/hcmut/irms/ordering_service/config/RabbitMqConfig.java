package com.hcmut.irms.ordering_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ topology for the ordering-service.
 *
 * Exchange:  restaurant.events  (TopicExchange — shared with kds-service, idempotent)
 * Publish:   routing key  order.created              → consumed by kds-service queue kds.order.created
 * Consume:   queue        ordering.kds.status         → bound to order.status.updated (published by kds-service)
 */
@Configuration
public class RabbitMqConfig {

    // ─── Exchange (shared with kds-service) ──────────────────────────────────

    @Bean
    public TopicExchange restaurantEventsExchange(
            @Value("${app.rabbitmq.exchange:restaurant.events}") String exchange) {
        return new TopicExchange(exchange);
    }

    // ─── Queue we consume: KDS status updates ────────────────────────────────

    @Bean
    public Queue orderStatusQueue(
            @Value("${app.rabbitmq.order-status-queue:ordering.kds.status}") String queueName) {
        return new Queue(queueName, true); // durable
    }

    @Bean
    public Binding orderStatusBinding(
            Queue orderStatusQueue,
            TopicExchange restaurantEventsExchange,
            @Value("${app.rabbitmq.order-status-routing-key:order.status.updated}") String routingKey) {
        return BindingBuilder.bind(orderStatusQueue).to(restaurantEventsExchange).with(routingKey);
    }

    // ─── JSON message converter ───────────────────────────────────────────────

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter rabbitMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(rabbitMessageConverter);
        return template;
    }
}
