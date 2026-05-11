package com.hcmut.irms.kds_service.application.port.in;

import com.hcmut.irms.kds_service.infrastructure.messaging.event.OrderCreatedEvent;

public interface CreateTicketUseCase {
    void createTicketFromEvent(OrderCreatedEvent event);
}
