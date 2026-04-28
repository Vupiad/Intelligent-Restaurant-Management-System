package com.hcmut.irms.kds_service.application.port.out;

import com.hcmut.irms.kds_service.domain.model.TicketStatus;

public interface OrderStatusPublisher {
    void publishOrderStatusEvent(String orderId, TicketStatus status);
}
