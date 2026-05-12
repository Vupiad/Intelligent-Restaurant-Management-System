package com.hcmut.irms.ordering_service.usecase.create;

import com.hcmut.irms.ordering_service.domain.Order;
import com.hcmut.irms.ordering_service.dto.event.OrderCreatedEvent;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Component
public class OrderCreatedEventFactory {
    public OrderCreatedEvent from(Order order) {
        String timestamp = OffsetDateTime.now(ZoneOffset.UTC).toString();

        List<OrderCreatedEvent.OrderItemPayload> payloads = order.getItems().stream()
                .map(i -> new OrderCreatedEvent.OrderItemPayload(
                        i.getMenuItemId(),
                        i.getName(),
                        i.getQuantity(),
                        i.getCustomizations(),
                        i.getNotes()))
                .toList();

        return new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                String.valueOf(order.getId()),
                toKdsTableNumber(order.getTableNumber()),
                order.getStaffName(),
                timestamp,
                payloads
        );
    }

    private int toKdsTableNumber(String tableNumber) {
        try {
            return Integer.parseInt(tableNumber);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
