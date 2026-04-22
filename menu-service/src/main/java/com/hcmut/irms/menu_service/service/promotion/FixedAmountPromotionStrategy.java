package com.hcmut.irms.menu_service.service.promotion;

import com.hcmut.irms.menu_service.model.PromotionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FixedAmountPromotionStrategy implements PromotionStrategy {
    @Override
    public BigDecimal calculateDiscount(BigDecimal basePrice, BigDecimal discountValue) {
        return basePrice.subtract(discountValue).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean supports(PromotionType type) {
        return type == PromotionType.FIXED_AMOUNT;
    }
}
