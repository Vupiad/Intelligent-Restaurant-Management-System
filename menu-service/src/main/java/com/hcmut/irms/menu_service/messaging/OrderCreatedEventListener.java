package com.hcmut.irms.menu_service.messaging;

import com.hcmut.irms.menu_service.messaging.event.OrderCreatedEvent;
import com.hcmut.irms.menu_service.service.OrderMenuAvailabilityCommand;
import com.hcmut.irms.menu_service.usecase.ConfirmOrderMenuAvailabilityUseCase;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderCreatedEventListener {
    private final ConfirmOrderMenuAvailabilityUseCase confirmOrderMenuAvailabilityUseCase;

    public OrderCreatedEventListener(ConfirmOrderMenuAvailabilityUseCase confirmOrderMenuAvailabilityUseCase) {
        this.confirmOrderMenuAvailabilityUseCase = confirmOrderMenuAvailabilityUseCase;
    }

    @RabbitListener(queues = "${app.rabbitmq.order-created-queue:kds.order.created}")
    public void handle(OrderCreatedEvent event) {
        List<String> menuItemIds = event == null || event.items() == null
                ? null
                : event.items().stream()
                .map(OrderCreatedEvent.OrderItemPayload::menuItemId)
                .toList();

        confirmOrderMenuAvailabilityUseCase.confirmAvailability(
                new OrderMenuAvailabilityCommand(event == null ? null : event.orderId(), menuItemIds)
        );
    }
}
