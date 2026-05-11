package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.application.PromotionView;

import java.util.List;

public interface ListActivePromotionsUseCase {
    List<PromotionView> getActivePromotions();
}
