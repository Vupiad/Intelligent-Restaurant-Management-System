package com.hcmut.irms.ordering_service.dto.api;

import java.util.List;

/**
 * Represents a single order item returned in API responses.
 */
public record OrderItemResponse(
        Long id,
        String menuItemId,
        String name,
        Integer quantity,
        List<String> customizations
) {}
