package com.hcmut.irms.kds_service.application.port.in;

import com.hcmut.irms.kds_service.domain.model.TicketStatus;

public interface UpdateTicketStatusUseCase {
    void updateOrderStatus(String ticketId, TicketStatus status);
}
