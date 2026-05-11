package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.application.MenuItemView;

import java.util.List;

public interface ListAvailableMenuItemsUseCase {
    List<MenuItemView> getAvailableMenu();
}
