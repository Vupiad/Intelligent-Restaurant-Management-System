package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.dto.MenuItemRequestDTO;
import com.hcmut.irms.menu_service.dto.MenuItemResponseDTO;

import java.util.UUID;

public interface MenuWriteUseCase {
    MenuItemResponseDTO createItem(MenuItemRequestDTO request);

    MenuItemResponseDTO updateItem(UUID menuItemId, MenuItemRequestDTO request);

    void deleteItem(UUID menuItemId);

    void applyPromotionToItem(UUID menuItemId, UUID promotionId);

    void removePromotionFromItem(UUID menuItemId, UUID promotionId);
}
