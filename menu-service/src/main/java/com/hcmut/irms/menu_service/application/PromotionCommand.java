package com.hcmut.irms.menu_service.application;

import com.hcmut.irms.menu_service.model.PromotionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromotionCommand(
        String name,
        PromotionType type,
        BigDecimal discountValue,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
