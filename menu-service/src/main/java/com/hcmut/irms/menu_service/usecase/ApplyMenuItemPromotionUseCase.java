package com.hcmut.irms.menu_service.usecase;

import java.util.UUID;

public interface ApplyMenuItemPromotionUseCase {
    void applyPromotionToItem(UUID menuItemId, UUID promotionId);
}
