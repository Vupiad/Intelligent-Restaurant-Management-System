package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.dto.MenuItemRequestDTO;
import com.hcmut.irms.menu_service.dto.MenuItemResponseDTO;
import com.hcmut.irms.menu_service.model.MenuItem;
import com.hcmut.irms.menu_service.model.Promotion;
import com.hcmut.irms.menu_service.repository.CategoryRepository;
import com.hcmut.irms.menu_service.repository.MenuItemRepository;
import com.hcmut.irms.menu_service.repository.PromotionRepository;
import com.hcmut.irms.menu_service.usecase.MenuWriteUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MenuWriteService implements MenuWriteUseCase {
    private final MenuItemRepository itemRepo;
    private final CategoryRepository categoryRepo;
    private final PromotionRepository promoRepo;
    private final PriceCalculationService priceService;

    public MenuWriteService(
            MenuItemRepository itemRepo,
            CategoryRepository categoryRepo,
            PromotionRepository promoRepo,
            PriceCalculationService priceService
    ) {
        this.itemRepo = itemRepo;
        this.categoryRepo = categoryRepo;
        this.promoRepo = promoRepo;
        this.priceService = priceService;
    }

    @Override
    @Transactional
    public MenuItemResponseDTO createItem(MenuItemRequestDTO request) {
        validateRequest(request);
        MenuItem item = new MenuItem();
        applyRequestToItem(item, request);
        MenuItem saved = itemRepo.save(item);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public MenuItemResponseDTO updateItem(UUID menuItemId, MenuItemRequestDTO request) {
        validateRequest(request);
        MenuItem existing = itemRepo.findByIdWithPromotions(menuItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found: " + menuItemId));

        applyRequestToItem(existing, request);
        MenuItem updated = itemRepo.save(existing);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteItem(UUID menuItemId) {
        MenuItem existing = itemRepo.findByIdWithPromotions(menuItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found: " + menuItemId));
        existing.getPromotions().clear();
        itemRepo.delete(existing);
    }

    @Override
    @Transactional
    public void applyPromotionToItem(UUID menuItemId, UUID promotionId) {
        MenuItem item = itemRepo.findByIdWithPromotions(menuItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found: " + menuItemId));

        Promotion promotion = promoRepo.findById(promotionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Promotion not found: " + promotionId));

        boolean alreadyApplied = item.getPromotions().stream()
                .anyMatch(existing -> existing.getId().equals(promotionId));
        if (!alreadyApplied) {
            item.getPromotions().add(promotion);
            itemRepo.save(item);
        }
    }

    @Override
    @Transactional
    public void removePromotionFromItem(UUID menuItemId, UUID promotionId) {
        MenuItem item = itemRepo.findByIdWithPromotions(menuItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found: " + menuItemId));

        boolean removed = item.getPromotions().removeIf(promotion -> promotion.getId().equals(promotionId));
        if (!removed) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Promotion " + promotionId + " is not linked to menu item " + menuItemId
            );
        }
        itemRepo.save(item);
    }

    private void validateRequest(MenuItemRequestDTO request) {
        if (request.getCategoryId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "categoryId is required");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        if (request.getBasePrice() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "basePrice is required");
        }
        if (request.getBasePrice().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "basePrice must be non-negative");
        }
        if (!categoryRepo.existsById(request.getCategoryId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found: " + request.getCategoryId());
        }
    }

    private void applyRequestToItem(MenuItem item, MenuItemRequestDTO request) {
        item.setCategory(categoryRepo.getReferenceById(request.getCategoryId()));
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setBasePrice(request.getBasePrice());
        item.setImageUrl(request.getImageUrl());
        item.setAvailable(request.getIsAvailable() == null || request.getIsAvailable());
        item.setCustomizations(request.getCustomizations() == null
                ? new ArrayList<>()
                : new ArrayList<>(request.getCustomizations()));
    }

    private MenuItemResponseDTO toResponse(MenuItem item) {
        LocalDateTime now = LocalDateTime.now();
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
