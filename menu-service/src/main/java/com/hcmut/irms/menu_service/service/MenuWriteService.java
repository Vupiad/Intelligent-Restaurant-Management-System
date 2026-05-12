package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.application.MenuItemCommand;
import com.hcmut.irms.menu_service.application.MenuItemView;
import com.hcmut.irms.menu_service.exception.MenuNotFoundException;
import com.hcmut.irms.menu_service.mapper.MenuItemMapper;
import com.hcmut.irms.menu_service.model.Category;
import com.hcmut.irms.menu_service.model.MenuItem;
import com.hcmut.irms.menu_service.model.Promotion;
import com.hcmut.irms.menu_service.port.CategoryReferenceProvider;
import com.hcmut.irms.menu_service.port.MenuItemPromotionReader;
import com.hcmut.irms.menu_service.port.MenuItemWriter;
import com.hcmut.irms.menu_service.port.PromotionReader;
import com.hcmut.irms.menu_service.usecase.ApplyMenuItemPromotionUseCase;
import com.hcmut.irms.menu_service.usecase.CreateMenuItemUseCase;
import com.hcmut.irms.menu_service.usecase.DeleteMenuItemUseCase;
import com.hcmut.irms.menu_service.usecase.RemoveMenuItemPromotionUseCase;
import com.hcmut.irms.menu_service.usecase.UpdateMenuItemUseCase;
import com.hcmut.irms.menu_service.validation.MenuItemRequestValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MenuWriteService implements CreateMenuItemUseCase, UpdateMenuItemUseCase, DeleteMenuItemUseCase,
        ApplyMenuItemPromotionUseCase, RemoveMenuItemPromotionUseCase {
    private final MenuItemPromotionReader itemPromotionReader;
    private final MenuItemWriter itemWriter;
    private final CategoryReferenceProvider categoryReferenceProvider;
    private final PromotionReader promotionReader;
    private final MenuItemRequestValidator requestValidator;
    private final MenuItemMapper menuItemMapper;

    public MenuWriteService(
            MenuItemPromotionReader itemPromotionReader,
            MenuItemWriter itemWriter,
            CategoryReferenceProvider categoryReferenceProvider,
            PromotionReader promotionReader,
            MenuItemRequestValidator requestValidator,
            MenuItemMapper menuItemMapper
    ) {
        this.itemPromotionReader = itemPromotionReader;
        this.itemWriter = itemWriter;
        this.categoryReferenceProvider = categoryReferenceProvider;
        this.promotionReader = promotionReader;
        this.requestValidator = requestValidator;
        this.menuItemMapper = menuItemMapper;
    }

    @Override
    @Transactional
    public MenuItemView createItem(MenuItemCommand command) {
        requestValidator.validate(command);
        MenuItem item = new MenuItem();
        applyCommandToItem(item, command);
        MenuItem saved = itemWriter.save(item);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public MenuItemView updateItem(UUID menuItemId, MenuItemCommand command) {
        requestValidator.validate(command);
        MenuItem existing = itemPromotionReader.findByIdWithPromotions(menuItemId)
                .orElseThrow(() -> new MenuNotFoundException("Menu item not found: " + menuItemId));

        applyCommandToItem(existing, command);
        MenuItem updated = itemWriter.save(existing);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteItem(UUID menuItemId) {
        MenuItem existing = itemPromotionReader.findByIdWithPromotions(menuItemId)
                .orElseThrow(() -> new MenuNotFoundException("Menu item not found: " + menuItemId));
        existing.getPromotions().clear();
        itemWriter.delete(existing);
    }

    @Override
    @Transactional
    public void applyPromotionToItem(UUID menuItemId, UUID promotionId) {
        MenuItem item = itemPromotionReader.findByIdWithPromotions(menuItemId)
                .orElseThrow(() -> new MenuNotFoundException("Menu item not found: " + menuItemId));

        Promotion promotion = promotionReader.findById(promotionId)
                .orElseThrow(() -> new MenuNotFoundException("Promotion not found: " + promotionId));

        boolean alreadyApplied = item.getPromotions().stream()
                .anyMatch(existing -> existing.getId().equals(promotionId));
        if (!alreadyApplied) {
            item.getPromotions().add(promotion);
            itemWriter.save(item);
        }
    }

    @Override
    @Transactional
    public void removePromotionFromItem(UUID menuItemId, UUID promotionId) {
        MenuItem item = itemPromotionReader.findByIdWithPromotions(menuItemId)
                .orElseThrow(() -> new MenuNotFoundException("Menu item not found: " + menuItemId));

        boolean removed = item.getPromotions().removeIf(promotion -> promotion.getId().equals(promotionId));
        if (!removed) {
            throw new MenuNotFoundException(
                    "Promotion " + promotionId + " is not linked to menu item " + menuItemId
            );
        }
        itemWriter.save(item);
    }

    private void applyCommandToItem(MenuItem item, MenuItemCommand command) {
        Category category = categoryReferenceProvider.getReferenceById(command.categoryId());
        menuItemMapper.applyToItem(item, command, category);
    }

    private MenuItemView toResponse(MenuItem item) {
        return menuItemMapper.toView(item, LocalDateTime.now());
    }
}
