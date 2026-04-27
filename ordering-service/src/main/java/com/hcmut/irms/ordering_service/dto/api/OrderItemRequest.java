package com.hcmut.irms.ordering_service.dto.api;

import java.util.List;

/**
 * Represents a single item inside a CreateOrderRequest.
 */
public record OrderItemRequest(
        String menuItemId,
        String name,
        Integer quantity,
        List<String> customizations
) {}
