package com.hcmut.irms.menu_service.mapper;

import com.hcmut.irms.menu_service.application.CategoryCommand;
import com.hcmut.irms.menu_service.application.CategoryView;
import com.hcmut.irms.menu_service.application.MenuItemAvailabilityView;
import com.hcmut.irms.menu_service.application.MenuItemCommand;
import com.hcmut.irms.menu_service.application.MenuItemView;
import com.hcmut.irms.menu_service.application.PromotionCommand;
import com.hcmut.irms.menu_service.application.PromotionView;
import com.hcmut.irms.menu_service.dto.CategoryRequestDTO;
import com.hcmut.irms.menu_service.dto.CategoryResponseDTO;
import com.hcmut.irms.menu_service.dto.MenuItemAvailabilityResponseDTO;
import com.hcmut.irms.menu_service.dto.MenuItemRequestDTO;
import com.hcmut.irms.menu_service.dto.MenuItemResponseDTO;
import com.hcmut.irms.menu_service.dto.PromotionRequestDTO;
import com.hcmut.irms.menu_service.dto.PromotionResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class MenuApiMapper {
    public CategoryCommand toCommand(CategoryRequestDTO request) {
        return new CategoryCommand(
                request.getName(),
                request.getDescription(),
                request.getIsActive()
        );
    }

    public CategoryResponseDTO toResponse(CategoryView view) {
        CategoryResponseDTO response = new CategoryResponseDTO();
        response.setId(view.id());
        response.setName(view.name());
        response.setDescription(view.description());
        response.setActive(view.active());
        return response;
    }

    public MenuItemCommand toCommand(MenuItemRequestDTO request) {
        return new MenuItemCommand(
                request.getCategoryId(),
                request.getName(),
                request.getDescription(),
                request.getBasePrice(),
                request.getIsAvailable(),
                request.getImageUrl(),
                request.getCustomizations()
        );
    }

    public MenuItemResponseDTO toResponse(MenuItemView view) {
        MenuItemResponseDTO response = new MenuItemResponseDTO();
        response.setId(view.id());
        response.setCategoryId(view.categoryId());
        response.setName(view.name());
        response.setDescription(view.description());
        response.setOriginalPrice(view.originalPrice());
        response.setFinalCalculatedPrice(view.finalCalculatedPrice());
        response.setAvailable(view.available());
        response.setImageUrl(view.imageUrl());
        response.setCustomizations(view.customizations());
        response.setActivePromotions(view.activePromotions());
        return response;
    }

    public MenuItemAvailabilityResponseDTO toResponse(MenuItemAvailabilityView view) {
        MenuItemAvailabilityResponseDTO response = new MenuItemAvailabilityResponseDTO();
        response.setItemId(view.itemId());
        response.setAvailableForOrder(view.availableForOrder());
        return response;
    }

    public PromotionCommand toCommand(PromotionRequestDTO request) {
        return new PromotionCommand(
                request.getName(),
                request.getType(),
                request.getDiscountValue(),
                request.getStartTime(),
                request.getEndTime()
        );
    }

    public PromotionResponseDTO toResponse(PromotionView view) {
        PromotionResponseDTO response = new PromotionResponseDTO();
        response.setId(view.id());
        response.setName(view.name());
        response.setType(view.type());
        response.setDiscountValue(view.discountValue());
        response.setStartTime(view.startTime());
        response.setEndTime(view.endTime());
        response.setActive(view.active());
        return response;
    }
}
