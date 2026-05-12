package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.application.PromotionCommand;
import com.hcmut.irms.menu_service.application.PromotionView;

public interface CreatePromotionUseCase {
    PromotionView createPromotion(PromotionCommand command);
}
