package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.application.MenuItemAvailabilityView;

import java.util.UUID;

public interface GetMenuItemAvailabilityUseCase {
    MenuItemAvailabilityView getItemAvailability(UUID itemId);
}
