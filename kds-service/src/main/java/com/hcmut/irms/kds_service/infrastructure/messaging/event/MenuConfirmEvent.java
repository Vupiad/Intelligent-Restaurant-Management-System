package com.hcmut.irms.kds_service.infrastructure.messaging.event;

public record MenuConfirmEvent(
        String orderId,
        boolean isAvailable
) {
}
