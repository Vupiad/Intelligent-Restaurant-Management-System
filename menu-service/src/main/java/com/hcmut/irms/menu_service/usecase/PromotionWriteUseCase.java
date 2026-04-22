package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.dto.PromotionRequestDTO;
import com.hcmut.irms.menu_service.dto.PromotionResponseDTO;

import java.util.UUID;

public interface PromotionWriteUseCase {
    PromotionResponseDTO createPromotion(PromotionRequestDTO request);

    PromotionResponseDTO updatePromotion(UUID promotionId, PromotionRequestDTO request);

    void deletePromotion(UUID promotionId);
}
