package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.dto.MenuItemResponseDTO;
import com.hcmut.irms.menu_service.dto.MenuItemAvailabilityResponseDTO;

import java.util.List;
import java.util.UUID;

public interface MenuReadUseCase {
    List<MenuItemResponseDTO> getAllMenuItems();

    List<MenuItemResponseDTO> getAvailableMenu();

    MenuItemResponseDTO getMenuItemById(UUID itemId);

    MenuItemAvailabilityResponseDTO getItemAvailability(UUID itemId);
}
