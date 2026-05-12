package com.hcmut.irms.menu_service.mapper;

import com.hcmut.irms.menu_service.application.MenuItemAvailabilityView;
import com.hcmut.irms.menu_service.application.MenuItemCommand;
import com.hcmut.irms.menu_service.application.MenuItemView;
import com.hcmut.irms.menu_service.model.Category;
import com.hcmut.irms.menu_service.model.MenuItem;
import com.hcmut.irms.menu_service.model.Promotion;
import com.hcmut.irms.menu_service.service.PriceCalculationService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MenuItemMapper {
    private final PriceCalculationService priceService;

    public MenuItemMapper(PriceCalculationService priceService) {
        this.priceService = priceService;
    }

    public void applyToItem(MenuItem item, MenuItemCommand command, Category category) {
        item.setCategory(category);
        item.setName(command.name());
        item.setDescription(command.description());
        item.setBasePrice(command.basePrice());
        item.setImageUrl(command.imageUrl());
        item.setAvailable(command.available() == null || command.available());
        item.setCustomizations(command.customizations() == null
                ? new ArrayList<>()
                : new ArrayList<>(command.customizations()));
    }

    public MenuItemView toView(MenuItem item, LocalDateTime now) {
        List<String> activePromotionNames = priceService.getActivePromotions(item, now).stream()
                .map(Promotion::getName)
                .toList();

        return new MenuItemView(
                item.getId(),
                item.getCategory().getId(),
                item.getName(),
                item.getDescription(),
                item.getBasePrice(),
                priceService.calculateFinalPrice(item, now),
                item.isAvailable(),
                item.getImageUrl(),
                item.getCustomizations(),
                activePromotionNames
        );
    }

    public MenuItemAvailabilityView toAvailabilityView(MenuItem item) {
        return new MenuItemAvailabilityView(item.getId(), item.isAvailable());
    }
}
