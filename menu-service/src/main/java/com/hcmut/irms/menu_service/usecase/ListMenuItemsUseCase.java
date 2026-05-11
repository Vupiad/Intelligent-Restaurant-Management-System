package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.application.MenuItemView;

import java.util.List;

public interface ListMenuItemsUseCase {
    List<MenuItemView> getAllMenuItems();
}
