package com.hcmut.irms.menu_service.controller;

import com.hcmut.irms.menu_service.dto.CategoryRequestDTO;
import com.hcmut.irms.menu_service.dto.CategoryResponseDTO;
import com.hcmut.irms.menu_service.usecase.CategoryReadUseCase;
import com.hcmut.irms.menu_service.usecase.CategoryWriteUseCase;
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
@RequestMapping("/api/menu/categories")
public class CategoryController {
    private final CategoryReadUseCase readUseCase;
    private final CategoryWriteUseCase writeUseCase;

    public CategoryController(CategoryReadUseCase readUseCase, CategoryWriteUseCase writeUseCase) {
        this.readUseCase = readUseCase;
        this.writeUseCase = writeUseCase;
    }

    @GetMapping
    public List<CategoryResponseDTO> getAllCategories() {
        return readUseCase.getAllCategories();
    }

    @GetMapping("/{categoryId}")
    public CategoryResponseDTO getCategory(@PathVariable UUID categoryId) {
        return readUseCase.getCategoryById(categoryId);
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO request) {
        CategoryResponseDTO created = writeUseCase.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{categoryId}")
    public CategoryResponseDTO updateCategory(@PathVariable UUID categoryId, @RequestBody CategoryRequestDTO request) {
        return writeUseCase.updateCategory(categoryId, request);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID categoryId) {
        writeUseCase.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
