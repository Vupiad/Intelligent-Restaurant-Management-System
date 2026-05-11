package com.hcmut.irms.menu_service.mapper;

import com.hcmut.irms.menu_service.dto.PromotionRequestDTO;
import com.hcmut.irms.menu_service.dto.PromotionResponseDTO;
import com.hcmut.irms.menu_service.model.Promotion;
import org.springframework.stereotype.Component;

@Component
public class PromotionMapper {
    public void applyToPromotion(Promotion promotion, PromotionRequestDTO request) {
        promotion.setName(request.getName());
        promotion.setType(request.getType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setStartTime(request.getStartTime());
        promotion.setEndTime(request.getEndTime());
    }

    public PromotionResponseDTO toResponse(Promotion promotion) {
        PromotionResponseDTO response = new PromotionResponseDTO();
        response.setId(promotion.getId());
        response.setName(promotion.getName());
        response.setType(promotion.getType());
        response.setDiscountValue(promotion.getDiscountValue());
        response.setStartTime(promotion.getStartTime());
        response.setEndTime(promotion.getEndTime());
        response.setActive(promotion.isActive());
        return response;
    }
}
