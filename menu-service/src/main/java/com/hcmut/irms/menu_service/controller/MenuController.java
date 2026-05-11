package com.hcmut.irms.menu_service.controller;

import com.hcmut.irms.menu_service.dto.MenuItemAvailabilityResponseDTO;
import com.hcmut.irms.menu_service.dto.MenuItemRequestDTO;
import com.hcmut.irms.menu_service.dto.MenuItemResponseDTO;
import com.hcmut.irms.menu_service.mapper.MenuApiMapper;
import com.hcmut.irms.menu_service.usecase.ApplyMenuItemPromotionUseCase;
import com.hcmut.irms.menu_service.usecase.CreateMenuItemUseCase;
import com.hcmut.irms.menu_service.usecase.DeleteMenuItemUseCase;
import com.hcmut.irms.menu_service.usecase.GetMenuItemAvailabilityUseCase;
import com.hcmut.irms.menu_service.usecase.GetMenuItemUseCase;
import com.hcmut.irms.menu_service.usecase.ListAvailableMenuItemsUseCase;
import com.hcmut.irms.menu_service.usecase.ListMenuItemsUseCase;
import com.hcmut.irms.menu_service.usecase.RemoveMenuItemPromotionUseCase;
import com.hcmut.irms.menu_service.usecase.UpdateMenuItemUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/menu")
public class MenuController {
    private final ListMenuItemsUseCase listMenuItemsUseCase;
    private final ListAvailableMenuItemsUseCase listAvailableMenuItemsUseCase;
    private final GetMenuItemUseCase getMenuItemUseCase;
    private final GetMenuItemAvailabilityUseCase getMenuItemAvailabilityUseCase;
    private final CreateMenuItemUseCase createMenuItemUseCase;
    private final UpdateMenuItemUseCase updateMenuItemUseCase;
    private final DeleteMenuItemUseCase deleteMenuItemUseCase;
    private final ApplyMenuItemPromotionUseCase applyMenuItemPromotionUseCase;
    private final RemoveMenuItemPromotionUseCase removeMenuItemPromotionUseCase;
    private final MenuApiMapper apiMapper;

    public MenuController(ListMenuItemsUseCase listMenuItemsUseCase,
                          ListAvailableMenuItemsUseCase listAvailableMenuItemsUseCase,
                          GetMenuItemUseCase getMenuItemUseCase,
                          GetMenuItemAvailabilityUseCase getMenuItemAvailabilityUseCase,
                          CreateMenuItemUseCase createMenuItemUseCase,
                          UpdateMenuItemUseCase updateMenuItemUseCase,
                          DeleteMenuItemUseCase deleteMenuItemUseCase,
                          ApplyMenuItemPromotionUseCase applyMenuItemPromotionUseCase,
                          RemoveMenuItemPromotionUseCase removeMenuItemPromotionUseCase,
                          MenuApiMapper apiMapper) {
        this.listMenuItemsUseCase = listMenuItemsUseCase;
        this.listAvailableMenuItemsUseCase = listAvailableMenuItemsUseCase;
        this.getMenuItemUseCase = getMenuItemUseCase;
        this.getMenuItemAvailabilityUseCase = getMenuItemAvailabilityUseCase;
        this.createMenuItemUseCase = createMenuItemUseCase;
        this.updateMenuItemUseCase = updateMenuItemUseCase;
        this.deleteMenuItemUseCase = deleteMenuItemUseCase;
        this.applyMenuItemPromotionUseCase = applyMenuItemPromotionUseCase;
        this.removeMenuItemPromotionUseCase = removeMenuItemPromotionUseCase;
        this.apiMapper = apiMapper;
    }

    @GetMapping
    public List<MenuItemResponseDTO> getAllMenuItems() {
        return listMenuItemsUseCase.getAllMenuItems().stream()
                .map(apiMapper::toResponse)
                .toList();
    }

    @GetMapping("/available")
    public List<MenuItemResponseDTO> getMenu() {
        return listAvailableMenuItemsUseCase.getAvailableMenu().stream()
                .map(apiMapper::toResponse)
                .toList();
    }

    @GetMapping("/{itemId}")
    public MenuItemResponseDTO getMenuItem(@PathVariable UUID itemId) {
        return apiMapper.toResponse(getMenuItemUseCase.getMenuItemById(itemId));
    }

    @GetMapping("/{itemId}/availability")
    public MenuItemAvailabilityResponseDTO getItemAvailability(@PathVariable UUID itemId) {
        return apiMapper.toResponse(getMenuItemAvailabilityUseCase.getItemAvailability(itemId));
    }

    @PostMapping
    public ResponseEntity<MenuItemResponseDTO> createItem(@RequestBody MenuItemRequestDTO request) {
        MenuItemResponseDTO created = apiMapper.toResponse(createMenuItemUseCase.createItem(apiMapper.toCommand(request)));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{itemId}")
    public MenuItemResponseDTO updateItem(@PathVariable UUID itemId, @RequestBody MenuItemRequestDTO request) {
        return apiMapper.toResponse(updateMenuItemUseCase.updateItem(itemId, apiMapper.toCommand(request)));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID itemId) {
        deleteMenuItemUseCase.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{itemId}/promotions/{promotionId}")
    public ResponseEntity<Void> applyPromo(@PathVariable UUID itemId, @PathVariable UUID promotionId) {
        applyMenuItemPromotionUseCase.applyPromotionToItem(itemId, promotionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{itemId}/promotions/{promotionId}")
    public ResponseEntity<Void> removePromo(@PathVariable UUID itemId, @PathVariable UUID promotionId) {
        removeMenuItemPromotionUseCase.removePromotionFromItem(itemId, promotionId);
        return ResponseEntity.noContent().build();
    }
}
