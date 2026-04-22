package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.dto.MenuItemResponseDTO;

import java.util.List;
import java.util.UUID;

public interface MenuReadUseCase {
    List<MenuItemResponseDTO> getAllMenuItems();

    List<MenuItemResponseDTO> getAvailableMenu();

    MenuItemResponseDTO getMenuItemById(UUID itemId);
}
