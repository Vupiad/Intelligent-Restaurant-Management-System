package com.hcmut.irms.menu_service.application;

import java.util.UUID;

public record MenuItemAvailabilityView(
        UUID itemId,
        boolean availableForOrder
) {
}
