package com.hcmut.irms.kds_service.infrastructure.messaging.event;

public record TicketReadyEvent(
        String eventId,
        String orderId,
        String newStatus,
        String timestamp,
        String updatedBy
) {
}
