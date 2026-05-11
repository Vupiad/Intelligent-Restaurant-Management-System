package com.hcmut.irms.menu_service.messaging;

import com.hcmut.irms.menu_service.messaging.event.OrderCreatedEvent;
import com.hcmut.irms.menu_service.service.OrderMenuAvailabilityCommand;
import com.hcmut.irms.menu_service.service.OrderMenuAvailabilityService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderCreatedEventListener {
    private final OrderMenuAvailabilityService orderMenuAvailabilityService;

    public OrderCreatedEventListener(OrderMenuAvailabilityService orderMenuAvailabilityService) {
        this.orderMenuAvailabilityService = orderMenuAvailabilityService;
    }

    @RabbitListener(queues = "${app.rabbitmq.order-created-queue:kds.order.created}")
    public void handle(OrderCreatedEvent event) {
        List<String> menuItemIds = event == null || event.items() == null
                ? null
                : event.items().stream()
                .map(OrderCreatedEvent.OrderItemPayload::menuItemId)
                .toList();

        orderMenuAvailabilityService.confirmAvailability(
                new OrderMenuAvailabilityCommand(event == null ? null : event.orderId(), menuItemIds)
        );
    }
}
