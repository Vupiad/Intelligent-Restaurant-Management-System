package com.hcmut.irms.menu_service.messaging;

import com.hcmut.irms.menu_service.port.MenuConfirmationPublisherPort;
import com.hcmut.irms.menu_service.messaging.event.MenuConfirmEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MenuConfirmPublisher implements MenuConfirmationPublisherPort {
    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public MenuConfirmPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.exchange:restaurant.events}") String exchange,
            @Value("${app.rabbitmq.menu-confirm-routing-key:menu.confirmed}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public void publish(String orderId, boolean isAvailable) {
        rabbitTemplate.convertAndSend(exchange, routingKey, new MenuConfirmEvent(orderId, isAvailable));
    }
}
