package com.hcmut.irms.ordering_service.dto.api;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST API response for a single order. Never exposes the JPA entity directly.
 */
public record OrderResponse(
        Long id,
        String tableNumber,
        String staffName,
        String status,
        LocalDateTime timestamp,
        List<OrderItemResponse> items
) {}
