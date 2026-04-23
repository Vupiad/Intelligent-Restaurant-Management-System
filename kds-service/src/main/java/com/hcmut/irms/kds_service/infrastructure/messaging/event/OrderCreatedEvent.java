package com.hcmut.irms.kds_service.infrastructure.messaging.event;

import java.util.List;

public record OrderCreatedEvent(
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
    ) {
    }
}
