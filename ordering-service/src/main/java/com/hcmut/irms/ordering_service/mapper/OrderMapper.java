package com.hcmut.irms.ordering_service.mapper;

import com.hcmut.irms.ordering_service.domain.Order;
import com.hcmut.irms.ordering_service.domain.OrderItem;
import com.hcmut.irms.ordering_service.usecase.model.CreateOrderCommand;
import com.hcmut.irms.ordering_service.usecase.model.OrderItemResult;
import com.hcmut.irms.ordering_service.usecase.model.OrderResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {
    public List<OrderItem> toOrderItems(CreateOrderCommand command) {
        return command.items().stream()
                .map(i -> OrderItem.builder()
                        .menuItemId(i.menuItemId())
                        .name(i.name())
                        .quantity(i.quantity())
                        .customizations(i.customizations() != null ? i.customizations() : new ArrayList<>())
                        .notes(i.notes() != null ? i.notes() : new ArrayList<>())
                        .build())
                .toList();
    }

    public OrderResult toResult(Order order) {
        List<OrderItemResult> itemResults = order.getItems().stream()
                .map(i -> new OrderItemResult(
                        i.getId(),
                        i.getMenuItemId(),
                        i.getName(),
                        i.getQuantity(),
                        i.getCustomizations(),
                        i.getNotes()))
                .toList();

        return new OrderResult(
                order.getId(),
                order.getTableNumber(),
                order.getStaffName(),
                order.getStatus().name(),
                order.getTimestamp(),
                itemResults
        );
    }
}
