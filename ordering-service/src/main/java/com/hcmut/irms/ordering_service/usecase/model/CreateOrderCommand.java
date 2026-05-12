package com.hcmut.irms.ordering_service.usecase.model;

import java.util.List;

public record CreateOrderCommand(
        String tableNumber,
        String staffName,
        List<OrderItemCommand> items
) {
}
