package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.dto.MenuItemRequestDTO;
import com.hcmut.irms.menu_service.dto.MenuItemResponseDTO;
import com.hcmut.irms.menu_service.mapper.MenuItemMapper;
import com.hcmut.irms.menu_service.model.Category;
import com.hcmut.irms.menu_service.model.MenuItem;
import com.hcmut.irms.menu_service.model.Promotion;
import com.hcmut.irms.menu_service.repository.CategoryRepository;
import com.hcmut.irms.menu_service.repository.MenuItemRepository;
import com.hcmut.irms.menu_service.repository.PromotionRepository;
import com.hcmut.irms.menu_service.usecase.MenuWriteUseCase;
import com.hcmut.irms.menu_service.validation.MenuItemRequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MenuWriteService implements MenuWriteUseCase {
    private final MenuItemRepository itemRepo;
    private final CategoryRepository categoryRepo;
    private final PromotionRepository promoRepo;
    private final MenuItemRequestValidator requestValidator;
    private final MenuItemMapper menuItemMapper;

    public MenuWriteService(
            MenuItemRepository itemRepo,
            CategoryRepository categoryRepo,
            PromotionRepository promoRepo,
            MenuItemRequestValidator requestValidator,
            MenuItemMapper menuItemMapper
    ) {
        this.itemRepo = itemRepo;
        this.categoryRepo = categoryRepo;
        this.promoRepo = promoRepo;
        this.requestValidator = requestValidator;
        this.menuItemMapper = menuItemMapper;
    }

    @Override
    @Transactional
    public MenuItemResponseDTO createItem(MenuItemRequestDTO request) {
        requestValidator.validate(request);
        MenuItem item = new MenuItem();
        applyRequestToItem(item, request);
        MenuItem saved = itemRepo.save(item);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public MenuItemResponseDTO updateItem(UUID menuItemId, MenuItemRequestDTO request) {
        requestValidator.validate(request);
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

    private void applyRequestToItem(MenuItem item, MenuItemRequestDTO request) {
        Category category = categoryRepo.getReferenceById(request.getCategoryId());
        menuItemMapper.applyToItem(item, request, category);
    }

    private MenuItemResponseDTO toResponse(MenuItem item) {
        return menuItemMapper.toResponse(item, LocalDateTime.now());
    }
}
