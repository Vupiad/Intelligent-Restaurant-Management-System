package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.application.PromotionView;

import java.util.UUID;

public interface GetPromotionUseCase {
    PromotionView getPromotionById(UUID promotionId);
}
