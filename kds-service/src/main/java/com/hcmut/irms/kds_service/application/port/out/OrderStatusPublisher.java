package com.hcmut.irms.kds_service.application.port.out;

public interface OrderStatusPublisher {
    void publishTicketReadyEvent(String orderId);
}
