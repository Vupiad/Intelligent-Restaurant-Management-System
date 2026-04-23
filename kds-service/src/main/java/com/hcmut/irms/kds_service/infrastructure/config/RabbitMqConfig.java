package com.hcmut.irms.kds_service.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Bean
    public TopicExchange restaurantEventsExchange(@Value("${app.rabbitmq.exchange:restaurant.events}") String exchange) {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue orderCreatedQueue(@Value("${app.rabbitmq.order-created-queue:kds.order.created}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding orderCreatedBinding(
            Queue orderCreatedQueue,
            TopicExchange restaurantEventsExchange,
            @Value("${app.rabbitmq.order-created-routing-key:order.created}") String routingKey) {
        return BindingBuilder.bind(orderCreatedQueue).to(restaurantEventsExchange).with(routingKey);
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
