package com.hcmut.irms.ordering_service.adapter.messaging;

import com.hcmut.irms.ordering_service.dto.event.KdsOrderCreatedEvent;
import com.hcmut.irms.ordering_service.port.OrderEventPublisherPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMQOrderEventPublisher implements OrderEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String orderCreatedRoutingKey;

    public RabbitMQOrderEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.exchange:restaurant.events}") String exchange,
            @Value("${app.rabbitmq.order-created-routing-key:order.created}") String orderCreatedRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.orderCreatedRoutingKey = orderCreatedRoutingKey;
    }

    @Override
    public void publishOrderCreated(KdsOrderCreatedEvent event) {
        log.info("Publishing order-created event for orderId={} to exchange={} routingKey={}",
                event.orderId(), exchange, orderCreatedRoutingKey);
        rabbitTemplate.convertAndSend(exchange, orderCreatedRoutingKey, event);
    }
}
