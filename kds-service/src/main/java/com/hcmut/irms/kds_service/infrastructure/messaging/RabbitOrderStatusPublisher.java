package com.hcmut.irms.kds_service.infrastructure.messaging;

import com.hcmut.irms.kds_service.application.port.out.OrderStatusPublisher;
import com.hcmut.irms.kds_service.infrastructure.messaging.event.TicketReadyEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class RabbitOrderStatusPublisher implements OrderStatusPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;
    private final String updatedBy;

    public RabbitOrderStatusPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.exchange:restaurant.events}") String exchange,
            @Value("${app.rabbitmq.order-status-updated-routing-key:order.status.updated}") String routingKey,
            @Value("${app.kds.station-id:KDS-Station-HotLine}") String updatedBy) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.updatedBy = updatedBy;
    }

    @Override
    public void publishTicketReadyEvent(String orderId) {
        TicketReadyEvent event = new TicketReadyEvent(
                UUID.randomUUID().toString(),
                orderId,
                "READY",
                OffsetDateTime.now(ZoneOffset.UTC).toString(),
                updatedBy
        );
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
