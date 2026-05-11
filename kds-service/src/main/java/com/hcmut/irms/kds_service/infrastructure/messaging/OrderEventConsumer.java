package com.hcmut.irms.kds_service.infrastructure.messaging;

import com.hcmut.irms.kds_service.application.port.in.ConfirmMenuAvailabilityUseCase;
import com.hcmut.irms.kds_service.application.port.in.CreateTicketUseCase;
import com.hcmut.irms.kds_service.infrastructure.messaging.event.MenuConfirmEvent;
import com.hcmut.irms.kds_service.infrastructure.messaging.event.OrderCreatedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {
    private final CreateTicketUseCase createTicketUseCase;
    private final ConfirmMenuAvailabilityUseCase confirmMenuAvailabilityUseCase;

    public OrderEventConsumer(CreateTicketUseCase createTicketUseCase,
                              ConfirmMenuAvailabilityUseCase confirmMenuAvailabilityUseCase) {
        this.createTicketUseCase = createTicketUseCase;
        this.confirmMenuAvailabilityUseCase = confirmMenuAvailabilityUseCase;
    }

    @RabbitListener(queues = "${app.rabbitmq.order-created-queue:kds.order.created}")
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        createTicketUseCase.createTicketFromEvent(event);
    }

    @RabbitListener(queues = "${app.rabbitmq.menu-confirm-queue:kds.menu.confirm}")
    public void consumeMenuConfirmEvent(MenuConfirmEvent event) {
        confirmMenuAvailabilityUseCase.confirmMenuAvailability(event.orderId(), event.isAvailable());
    }
}
