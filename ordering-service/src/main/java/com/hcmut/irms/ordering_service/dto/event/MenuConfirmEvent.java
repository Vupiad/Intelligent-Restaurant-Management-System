package com.hcmut.irms.ordering_service.dto.event;

public record MenuConfirmEvent(
        String orderId,
        boolean isAvailable
) {
}
