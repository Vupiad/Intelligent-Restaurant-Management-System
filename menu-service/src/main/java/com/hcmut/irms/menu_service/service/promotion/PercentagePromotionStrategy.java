package com.hcmut.irms.menu_service.service.promotion;

import com.hcmut.irms.menu_service.model.PromotionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PercentagePromotionStrategy implements PromotionStrategy {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    @Override
    public BigDecimal calculateDiscount(BigDecimal basePrice, BigDecimal discountValue) {
        BigDecimal multiplier = BigDecimal.ONE.subtract(discountValue.divide(ONE_HUNDRED, 4, RoundingMode.HALF_UP));
        return basePrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean supports(PromotionType type) {
        return type == PromotionType.PERCENTAGE;
    }
}
