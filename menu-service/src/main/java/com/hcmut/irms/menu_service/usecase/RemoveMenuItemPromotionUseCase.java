package com.hcmut.irms.menu_service.usecase;

import java.util.UUID;

public interface RemoveMenuItemPromotionUseCase {
    void removePromotionFromItem(UUID menuItemId, UUID promotionId);
}
