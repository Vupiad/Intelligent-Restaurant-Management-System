package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.dto.PromotionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface PromotionReadUseCase {
    List<PromotionResponseDTO> getAllPromotions();

    List<PromotionResponseDTO> getActivePromotions();

    PromotionResponseDTO getPromotionById(UUID promotionId);
}
