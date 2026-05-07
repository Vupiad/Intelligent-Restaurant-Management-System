package com.hcmut.irms.menu_service.messaging.event;

public record MenuConfirmEvent(
        String orderId,
        boolean isAvailable
) {
}
