package com.hcmut.irms.ordering_service.mapper;

import com.hcmut.irms.ordering_service.dto.api.CreateOrderRequest;
import com.hcmut.irms.ordering_service.dto.api.OrderItemResponse;
import com.hcmut.irms.ordering_service.dto.api.OrderResponse;
import com.hcmut.irms.ordering_service.usecase.model.CreateOrderCommand;
import com.hcmut.irms.ordering_service.usecase.model.OrderItemCommand;
import com.hcmut.irms.ordering_service.usecase.model.OrderResult;
import org.springframework.stereotype.Component;

@Component
public class OrderApiMapper {
    public CreateOrderCommand toCommand(CreateOrderRequest request) {
        return new CreateOrderCommand(
                request.tableNumber(),
                request.staffName(),
                request.items().stream()
                        .map(item -> new OrderItemCommand(
                                item.menuItemId(),
                                item.name(),
                                item.quantity(),
                                item.customizations(),
                                item.notes()
                        ))
                        .toList()
        );
    }

    public OrderResponse toResponse(OrderResult result) {
        return new OrderResponse(
                result.id(),
                result.tableNumber(),
                result.staffName(),
                result.status(),
                result.timestamp(),
                result.items().stream()
                        .map(item -> new OrderItemResponse(
                                item.id(),
                                item.menuItemId(),
                                item.name(),
                                item.quantity(),
                                item.customizations(),
                                item.notes()
                        ))
                        .toList()
        );
    }
}
