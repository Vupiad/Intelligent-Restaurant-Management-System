package com.hcmut.irms.kds_service.infrastructure.messaging;

import com.hcmut.irms.kds_service.application.port.in.TicketWriteUseCase;
import com.hcmut.irms.kds_service.infrastructure.messaging.event.OrderCreatedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {
    private final TicketWriteUseCase writeUseCase;

    public OrderEventConsumer(TicketWriteUseCase writeUseCase) {
        this.writeUseCase = writeUseCase;
    }

    @RabbitListener(queues = "${app.rabbitmq.order-created-queue:kds.order.created}")
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        writeUseCase.createTicketFromEvent(event);
    }
}
