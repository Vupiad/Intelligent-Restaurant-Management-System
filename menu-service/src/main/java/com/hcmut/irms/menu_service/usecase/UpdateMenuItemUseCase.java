package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.application.MenuItemCommand;
import com.hcmut.irms.menu_service.application.MenuItemView;

import java.util.UUID;

public interface UpdateMenuItemUseCase {
    MenuItemView updateItem(UUID menuItemId, MenuItemCommand command);
}
