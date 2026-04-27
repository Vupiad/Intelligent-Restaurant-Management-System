package com.hcmut.irms.ordering_service.dto.event;

/**
 * Event consumed from KDS when a kitchen ticket becomes ready.
 * <p>
 * Field names MUST match exactly what kds-service's {@code TicketReadyEvent} publishes:
 * {@code eventId, orderId, newStatus, timestamp, updatedBy}
 */
public record KdsStatusEvent(
        String eventId,
        String orderId,
        String newStatus,
        String timestamp,
        String updatedBy
) {}
