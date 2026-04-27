package com.hcmut.irms.ordering_service.usecase.create;

import com.hcmut.irms.ordering_service.domain.Order;
import com.hcmut.irms.ordering_service.domain.OrderItem;
import com.hcmut.irms.ordering_service.domain.exception.MenuItemUnavailableException;
import com.hcmut.irms.ordering_service.dto.api.CreateOrderRequest;
import com.hcmut.irms.ordering_service.dto.api.OrderItemResponse;
import com.hcmut.irms.ordering_service.dto.api.OrderResponse;
import com.hcmut.irms.ordering_service.dto.event.KdsOrderCreatedEvent;
import com.hcmut.irms.ordering_service.port.MenuAvailabilityPort;
import com.hcmut.irms.ordering_service.port.OrderEventPublisherPort;
import com.hcmut.irms.ordering_service.port.OrderRepositoryPort;
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

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String bearerToken) {
        // 1. Check menu availability (via MenuAvailabilityPort — no RabbitMQ or JPA here)
        List<String> itemIds = request.items().stream()
                .map(i -> i.menuItemId())
                .distinct()
                .toList();

        List<String> unavailable = menuAvailabilityPort.findUnavailableItemIds(itemIds, bearerToken);
        if (!unavailable.isEmpty()) {
            throw new MenuItemUnavailableException(unavailable);
        }

        // 2. Build domain objects
        List<OrderItem> domainItems = request.items().stream()
                .map(i -> OrderItem.builder()
                        .menuItemId(i.menuItemId())
                        .name(i.name())
                        .quantity(i.quantity())
                        .customizations(i.customizations() != null ? i.customizations() : new ArrayList<>())
                        .build())
                .toList();

        Order order = Order.create(request.tableNumber(), request.staffName(), domainItems);

        // 3. Persist
        Order saved = orderRepositoryPort.save(order);

        // 4. Publish to KDS via port (no direct RabbitMQ code here)
        KdsOrderCreatedEvent event = buildKdsEvent(saved);
        orderEventPublisherPort.publishOrderCreated(event);

        // 5. Map to response DTO
        return toResponse(saved);
    }

    // ─── Private helpers ─────────────────────────────────────────────────────

    private KdsOrderCreatedEvent buildKdsEvent(Order order) {
        String timestamp = OffsetDateTime.now(ZoneOffset.UTC).toString();

        List<KdsOrderCreatedEvent.OrderItemPayload> payloads = order.getItems().stream()
                .map(i -> new KdsOrderCreatedEvent.OrderItemPayload(
                        i.getMenuItemId(),
                        i.getName(),
                        i.getQuantity(),
                        i.getCustomizations()))
                .toList();

        // tableNumber is String in our domain; KDS expects Integer — parse safely
        Integer tableNum;
        try {
            tableNum = Integer.parseInt(order.getTableNumber());
        } catch (NumberFormatException e) {
            tableNum = 0;
        }

        return new KdsOrderCreatedEvent(
                UUID.randomUUID().toString(),
                String.valueOf(order.getId()),
                tableNum,
                order.getStaffName(),
                timestamp,
                payloads
        );
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(i -> new OrderItemResponse(
                        i.getId(),
                        i.getMenuItemId(),
                        i.getName(),
                        i.getQuantity(),
                        i.getCustomizations()))
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
