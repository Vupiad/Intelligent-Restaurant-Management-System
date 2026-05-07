package com.hcmut.irms.ordering_service.usecase.create;

import com.hcmut.irms.ordering_service.domain.Order;
import com.hcmut.irms.ordering_service.domain.OrderItem;
import com.hcmut.irms.ordering_service.domain.OutboxEvent;
import com.hcmut.irms.ordering_service.dto.api.CreateOrderRequest;
import com.hcmut.irms.ordering_service.dto.api.OrderItemResponse;
import com.hcmut.irms.ordering_service.dto.api.OrderResponse;
import com.hcmut.irms.ordering_service.dto.event.OrderCreatedEvent;
import com.hcmut.irms.ordering_service.port.MenuAvailabilityPort;
import com.hcmut.irms.ordering_service.port.OutboxEventRepositoryPort;
import com.hcmut.irms.ordering_service.port.OrderEventPublisherPort;
import com.hcmut.irms.ordering_service.port.OrderRepositoryPort;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final MenuAvailabilityPort menuAvailabilityPort;
    private final OrderEventPublisherPort orderEventPublisherPort;
    private final OutboxEventRepositoryPort outboxEventRepositoryPort;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String bearerToken) {

        List<OrderItem> orderItems = request.items().stream()
                .map(i -> OrderItem.builder()
                        .menuItemId(i.menuItemId())
                        .name(i.name())
                        .quantity(i.quantity())
                        .customizations(i.customizations() != null ? i.customizations() : new ArrayList<>())
                        .notes(i.notes() != null ? i.notes() : new ArrayList<>())
                        .build())
                .toList();

        Order order = Order.create(request.tableNumber(), request.staffName(), orderItems);

        Order saved = orderRepositoryPort.save(order);

        OrderCreatedEvent event = buildOrderCreatedEvent(saved);

        orderEventPublisherPort.publishOrderCreated(event);


        return toResponse(saved);
    }

    // ─── Private helpers ─────────────────────────────────────────────────────

    private OrderCreatedEvent buildOrderCreatedEvent(Order order) {
        String timestamp = OffsetDateTime.now(ZoneOffset.UTC).toString();

        List<OrderCreatedEvent.OrderItemPayload> payloads = order.getItems().stream()
                .map(i -> new OrderCreatedEvent.OrderItemPayload(
                        i.getMenuItemId(),
                        i.getName(),
                        i.getQuantity(),
                        i.getCustomizations(),
                        i.getNotes()))
                .toList();

        // tableNumber is String in our domain; KDS expects Integer — parse safely
        int tableNum;
        try {
            tableNum = Integer.parseInt(order.getTableNumber());
        } catch (NumberFormatException e) {
            tableNum = 0;
        }

        return new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                String.valueOf(order.getId()),
                tableNum,
                order.getStaffName(),
                timestamp,
                payloads
        );
    }

    private OutboxEvent buildOutboxEvent(OrderCreatedEvent event) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAggregateType("Order");
        outboxEvent.setEventType("OrderCreated");
        outboxEvent.setPayload(objectMapper.convertValue(event, new TypeReference<>() {}));
        return outboxEvent;
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(i -> new OrderItemResponse(
                        i.getId(),
                        i.getMenuItemId(),
                        i.getName(),
                        i.getQuantity(),
                        i.getCustomizations(),
                        i.getNotes()))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getTableNumber(),
                order.getStaffName(),
                order.getStatus().name(),
                order.getTimestamp(),
                itemResponses
        );
    }
}
