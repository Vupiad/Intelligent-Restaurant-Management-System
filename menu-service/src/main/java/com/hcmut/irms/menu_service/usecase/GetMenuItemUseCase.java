package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.application.MenuItemView;

import java.util.UUID;

public interface GetMenuItemUseCase {
    MenuItemView getMenuItemById(UUID itemId);
}
