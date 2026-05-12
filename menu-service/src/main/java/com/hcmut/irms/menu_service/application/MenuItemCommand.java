package com.hcmut.irms.menu_service.application;

import com.hcmut.irms.menu_service.model.Customization;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record MenuItemCommand(
        UUID categoryId,
        String name,
        String description,
        BigDecimal basePrice,
        Boolean available,
        String imageUrl,
        List<Customization> customizations
) {
}
