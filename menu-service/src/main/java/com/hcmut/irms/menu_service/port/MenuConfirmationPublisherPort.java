package com.hcmut.irms.menu_service.port;

public interface MenuConfirmationPublisherPort {
    void publish(String orderId, boolean isAvailable);
}
