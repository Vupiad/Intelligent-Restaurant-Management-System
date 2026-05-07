package com.hcmut.irms.ordering_service.dto.api;

public record OrderStatusRealtimeMessage(
        String orderId,
        String status
) {
}
