package com.hcmut.irms.ordering_service.mapper;

import com.hcmut.irms.ordering_service.domain.Order;
import com.hcmut.irms.ordering_service.domain.OrderItem;
import com.hcmut.irms.ordering_service.dto.api.CreateOrderRequest;
import com.hcmut.irms.ordering_service.dto.api.OrderItemResponse;
import com.hcmut.irms.ordering_service.dto.api.OrderResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {
    public List<OrderItem> toOrderItems(CreateOrderRequest request) {
        return request.items().stream()
                .map(i -> OrderItem.builder()
                        .menuItemId(i.menuItemId())
                        .name(i.name())
                        .quantity(i.quantity())
                        .customizations(i.customizations() != null ? i.customizations() : new ArrayList<>())
                        .notes(i.notes() != null ? i.notes() : new ArrayList<>())
                        .build())
                .toList();
    }

    public OrderResponse toResponse(Order order) {
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
