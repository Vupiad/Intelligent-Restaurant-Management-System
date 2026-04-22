package com.hcmut.irms.menu_service.service.promotion;

import com.hcmut.irms.menu_service.model.PromotionType;

import java.math.BigDecimal;

public interface PromotionStrategy {
    BigDecimal calculateDiscount(BigDecimal basePrice, BigDecimal discountValue);

    boolean supports(PromotionType type);
}
