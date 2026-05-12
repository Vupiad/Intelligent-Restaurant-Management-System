package com.hcmut.irms.ordering_service.usecase.model;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResult(
        Long id,
        String tableNumber,
        String staffName,
        String status,
        LocalDateTime timestamp,
        List<OrderItemResult> items
) {
}
