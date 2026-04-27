package com.hcmut.irms.ordering_service.dto.event;

import java.util.List;

/**
 * Event published to KDS when a new order is created.
 * <p>
 * Field names MUST match exactly what kds-service's {@code OrderCreatedEvent} expects:
 * {@code eventId, orderId, tableNumber (Integer), waiterId, timestamp (ISO-8601 string),
 * items[{menuItemId, itemName, quantity, customizations}]}
 */
public record KdsOrderCreatedEvent(
        String eventId,
        String orderId,
        Integer tableNumber,
        String waiterId,
        String timestamp,
        List<OrderItemPayload> items
) {
    public record OrderItemPayload(
            String menuItemId,
            String itemName,
            Integer quantity,
            List<String> customizations
    ) {}
}
