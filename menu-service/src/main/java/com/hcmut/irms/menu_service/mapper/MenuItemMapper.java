package com.hcmut.irms.menu_service.mapper;

import com.hcmut.irms.menu_service.dto.MenuItemAvailabilityResponseDTO;
import com.hcmut.irms.menu_service.dto.MenuItemRequestDTO;
import com.hcmut.irms.menu_service.dto.MenuItemResponseDTO;
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

    public void applyToItem(MenuItem item, MenuItemRequestDTO request, Category category) {
        item.setCategory(category);
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setBasePrice(request.getBasePrice());
        item.setImageUrl(request.getImageUrl());
        item.setAvailable(request.getIsAvailable() == null || request.getIsAvailable());
        item.setCustomizations(request.getCustomizations() == null
                ? new ArrayList<>()
                : new ArrayList<>(request.getCustomizations()));
    }

    public MenuItemResponseDTO toResponse(MenuItem item, LocalDateTime now) {
        List<String> activePromotionNames = priceService.getActivePromotions(item, now).stream()
                .map(Promotion::getName)
                .toList();

        MenuItemResponseDTO response = new MenuItemResponseDTO();
        response.setId(item.getId());
        response.setCategoryId(item.getCategory().getId());
        response.setName(item.getName());
        response.setDescription(item.getDescription());
        response.setOriginalPrice(item.getBasePrice());
        response.setFinalCalculatedPrice(priceService.calculateFinalPrice(item, now));
        response.setAvailable(item.isAvailable());
        response.setImageUrl(item.getImageUrl());
        response.setCustomizations(item.getCustomizations());
        response.setActivePromotions(activePromotionNames);
        return response;
    }

    public MenuItemAvailabilityResponseDTO toAvailabilityResponse(MenuItem item) {
        MenuItemAvailabilityResponseDTO response = new MenuItemAvailabilityResponseDTO();
        response.setItemId(item.getId());
        response.setAvailableForOrder(item.isAvailable());
        return response;
    }
}
