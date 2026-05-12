package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.application.PromotionCommand;
import com.hcmut.irms.menu_service.application.PromotionView;

import java.util.UUID;

public interface UpdatePromotionUseCase {
    PromotionView updatePromotion(UUID promotionId, PromotionCommand command);
}
