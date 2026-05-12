package com.hcmut.irms.kds_service.infrastructure.messaging;

import com.hcmut.irms.kds_service.application.command.CreateTicketCommand;
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
        createTicketUseCase.createTicket(toCommand(event));
    }

    @RabbitListener(queues = "${app.rabbitmq.menu-confirm-queue:kds.menu.confirm}")
    public void consumeMenuConfirmEvent(MenuConfirmEvent event) {
        confirmMenuAvailabilityUseCase.confirmMenuAvailability(event.orderId(), event.isAvailable());
    }

    private CreateTicketCommand toCommand(OrderCreatedEvent event) {
        return new CreateTicketCommand(
                event.orderId(),
                event.tableNumber(),
                event.waiterId(),
                event.timestamp(),
                event.items() == null ? null : event.items().stream()
                        .map(item -> new CreateTicketCommand.Item(
                                item.menuItemId(),
                                item.itemName(),
                                item.quantity(),
                                item.customizations(),
                                item.notes()
                        ))
                        .toList()
        );
    }
}
