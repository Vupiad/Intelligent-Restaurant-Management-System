package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.application.MenuItemAvailabilityView;
import com.hcmut.irms.menu_service.application.MenuItemView;
import com.hcmut.irms.menu_service.exception.MenuNotFoundException;
import com.hcmut.irms.menu_service.mapper.MenuItemMapper;
import com.hcmut.irms.menu_service.model.MenuItem;
import com.hcmut.irms.menu_service.port.MenuItemPromotionReader;
import com.hcmut.irms.menu_service.port.MenuItemReader;
import com.hcmut.irms.menu_service.usecase.GetMenuItemAvailabilityUseCase;
import com.hcmut.irms.menu_service.usecase.GetMenuItemUseCase;
import com.hcmut.irms.menu_service.usecase.ListAvailableMenuItemsUseCase;
import com.hcmut.irms.menu_service.usecase.ListMenuItemsUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MenuReadService implements ListMenuItemsUseCase, ListAvailableMenuItemsUseCase,
        GetMenuItemUseCase, GetMenuItemAvailabilityUseCase {
    private final MenuItemPromotionReader itemPromotionReader;
    private final MenuItemReader itemReader;
    private final MenuItemMapper menuItemMapper;

    public MenuReadService(MenuItemPromotionReader itemPromotionReader,
                           MenuItemReader itemReader,
                           MenuItemMapper menuItemMapper) {
        this.itemPromotionReader = itemPromotionReader;
        this.itemReader = itemReader;
        this.menuItemMapper = menuItemMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemView> getAllMenuItems() {
        LocalDateTime now = LocalDateTime.now();
        return itemPromotionReader.findAllWithPromotions().stream()
                .map(item -> menuItemMapper.toView(item, now))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemView> getAvailableMenu() {
        LocalDateTime now = LocalDateTime.now();
        return itemPromotionReader.findAvailableWithPromotions().stream()
                .map(item -> menuItemMapper.toView(item, now))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemView getMenuItemById(UUID itemId) {
        MenuItem item = itemPromotionReader.findByIdWithPromotions(itemId)
                .orElseThrow(() -> new MenuNotFoundException("Menu item not found: " + itemId));
        return menuItemMapper.toView(item, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemAvailabilityView getItemAvailability(UUID itemId) {
        MenuItem item = itemReader.findById(itemId)
                .orElseThrow(() -> new MenuNotFoundException("Menu item not found: " + itemId));

        return menuItemMapper.toAvailabilityView(item);
    }
}
