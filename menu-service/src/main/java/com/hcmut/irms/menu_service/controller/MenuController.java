package com.hcmut.irms.menu_service.controller;

import com.hcmut.irms.menu_service.dto.MenuItemRequestDTO;
import com.hcmut.irms.menu_service.dto.MenuItemResponseDTO;
import com.hcmut.irms.menu_service.usecase.MenuReadUseCase;
import com.hcmut.irms.menu_service.usecase.MenuWriteUseCase;
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
    private final MenuReadUseCase readUseCase;
    private final MenuWriteUseCase writeUseCase;

    public MenuController(MenuReadUseCase readUseCase, MenuWriteUseCase writeUseCase) {
        this.readUseCase = readUseCase;
        this.writeUseCase = writeUseCase;
    }

    @GetMapping
    public List<MenuItemResponseDTO> getAllMenuItems() {
        return readUseCase.getAllMenuItems();
    }

    @GetMapping("/available")
    public List<MenuItemResponseDTO> getMenu() {
        return readUseCase.getAvailableMenu();
    }

    @GetMapping("/{itemId}")
    public MenuItemResponseDTO getMenuItem(@PathVariable UUID itemId) {
        return readUseCase.getMenuItemById(itemId);
    }

    @PostMapping
    public ResponseEntity<MenuItemResponseDTO> createItem(@RequestBody MenuItemRequestDTO request) {
        MenuItemResponseDTO created = writeUseCase.createItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{itemId}")
    public MenuItemResponseDTO updateItem(@PathVariable UUID itemId, @RequestBody MenuItemRequestDTO request) {
        return writeUseCase.updateItem(itemId, request);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID itemId) {
        writeUseCase.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{itemId}/promotions/{promotionId}")
    public ResponseEntity<Void> applyPromo(@PathVariable UUID itemId, @PathVariable UUID promotionId) {
        writeUseCase.applyPromotionToItem(itemId, promotionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{itemId}/promotions/{promotionId}")
    public ResponseEntity<Void> removePromo(@PathVariable UUID itemId, @PathVariable UUID promotionId) {
        writeUseCase.removePromotionFromItem(itemId, promotionId);
        return ResponseEntity.noContent().build();
    }
}
