package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.dto.MenuItemResponseDTO;
import com.hcmut.irms.menu_service.model.MenuItem;
import com.hcmut.irms.menu_service.model.Promotion;
import com.hcmut.irms.menu_service.repository.MenuItemRepository;
import com.hcmut.irms.menu_service.usecase.MenuReadUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MenuReadService implements MenuReadUseCase {
    private final MenuItemRepository itemRepo;
    private final PriceCalculationService priceService;

    public MenuReadService(MenuItemRepository itemRepo, PriceCalculationService priceService) {
        this.itemRepo = itemRepo;
        this.priceService = priceService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponseDTO> getAllMenuItems() {
        LocalDateTime now = LocalDateTime.now();
        return itemRepo.findAllWithPromotions().stream()
                .map(item -> toResponse(item, now))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponseDTO> getAvailableMenu() {
        LocalDateTime now = LocalDateTime.now();
        return itemRepo.findAvailableWithPromotions().stream()
                .map(item -> toResponse(item, now))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemResponseDTO getMenuItemById(UUID itemId) {
        MenuItem item = itemRepo.findByIdWithPromotions(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found: " + itemId));
        return toResponse(item, LocalDateTime.now());
    }

    private MenuItemResponseDTO toResponse(MenuItem item, LocalDateTime now) {
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
}
