package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.model.MenuItem;
import com.hcmut.irms.menu_service.model.Promotion;
import com.hcmut.irms.menu_service.service.promotion.PromotionStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PriceCalculationService {
    private final List<PromotionStrategy> strategies;

    public PriceCalculationService(List<PromotionStrategy> strategies) {
        this.strategies = strategies;
    }

    public BigDecimal calculateFinalPrice(MenuItem item) {
        return calculateFinalPrice(item, LocalDateTime.now());
    }

    public BigDecimal calculateFinalPrice(MenuItem item, LocalDateTime now) {
        BigDecimal finalPrice = item.getBasePrice();
        for (Promotion promo : getActivePromotions(item, now)) {
            finalPrice = applyPromotion(finalPrice, promo);
        }
        return finalPrice;
    }

    public List<Promotion> getActivePromotions(MenuItem item, LocalDateTime now) {
        if (item.getPromotions() == null || item.getPromotions().isEmpty()) {
            return Collections.emptyList();
        }

        List<Promotion> activePromotions = new ArrayList<>();
        for (Promotion promotion : item.getPromotions()) {
            if (isActive(promotion, now)) {
                activePromotions.add(promotion);
            }
        }
        return activePromotions;
    }

    private boolean isActive(Promotion promotion, LocalDateTime now) {
        return promotion.isActive()
                && !now.isBefore(promotion.getStartTime())
                && !now.isAfter(promotion.getEndTime());
    }

    private BigDecimal applyPromotion(BigDecimal currentPrice, Promotion promotion) {
        for (PromotionStrategy strategy : strategies) {
            if (strategy.supports(promotion.getType())) {
                return strategy.calculateDiscount(currentPrice, promotion.getDiscountValue());
            }
        }
        throw new IllegalStateException("No promotion strategy found for type: " + promotion.getType());
    }
}
