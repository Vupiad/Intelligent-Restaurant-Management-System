package com.hcmut.irms.kds_service.application.port.in;

import com.hcmut.irms.kds_service.domain.model.ItemStatus;
import com.hcmut.irms.kds_service.domain.model.KitchenTicket;
import com.hcmut.irms.kds_service.domain.model.TicketStatus;
import com.hcmut.irms.kds_service.infrastructure.messaging.event.OrderCreatedEvent;

public interface TicketWriteUseCase {
    void createTicketFromEvent(OrderCreatedEvent event);

    void updateOrderStatus(String ticketId, TicketStatus status);
}
