package com.hcmut.irms.ordering_service.dto.api;

import java.util.List;

/**
 * REST API request body for creating a new order.
 */
public record CreateOrderRequest(
        String tableNumber,
        String staffName,
        List<OrderItemRequest> items
) {}
