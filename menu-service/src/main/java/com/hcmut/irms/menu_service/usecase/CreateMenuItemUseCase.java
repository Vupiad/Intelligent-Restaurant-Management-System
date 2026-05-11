package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.application.MenuItemCommand;
import com.hcmut.irms.menu_service.application.MenuItemView;

public interface CreateMenuItemUseCase {
    MenuItemView createItem(MenuItemCommand command);
}
