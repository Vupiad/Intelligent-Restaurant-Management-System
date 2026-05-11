package com.hcmut.irms.ordering_service.port;

public interface OrderStatusNotificationPort {
    void publish(String orderId, String status);
}
