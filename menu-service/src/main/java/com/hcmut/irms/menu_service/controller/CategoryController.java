package com.hcmut.irms.menu_service.controller;

import com.hcmut.irms.menu_service.dto.CategoryRequestDTO;
import com.hcmut.irms.menu_service.dto.CategoryResponseDTO;
import com.hcmut.irms.menu_service.mapper.MenuApiMapper;
import com.hcmut.irms.menu_service.usecase.CreateCategoryUseCase;
import com.hcmut.irms.menu_service.usecase.DeleteCategoryUseCase;
import com.hcmut.irms.menu_service.usecase.GetCategoryUseCase;
import com.hcmut.irms.menu_service.usecase.ListCategoriesUseCase;
import com.hcmut.irms.menu_service.usecase.UpdateCategoryUseCase;
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
    private final ListCategoriesUseCase listCategoriesUseCase;
    private final GetCategoryUseCase getCategoryUseCase;
    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final MenuApiMapper apiMapper;

    public CategoryController(ListCategoriesUseCase listCategoriesUseCase,
                              GetCategoryUseCase getCategoryUseCase,
                              CreateCategoryUseCase createCategoryUseCase,
                              UpdateCategoryUseCase updateCategoryUseCase,
                              DeleteCategoryUseCase deleteCategoryUseCase,
                              MenuApiMapper apiMapper) {
        this.listCategoriesUseCase = listCategoriesUseCase;
        this.getCategoryUseCase = getCategoryUseCase;
        this.createCategoryUseCase = createCategoryUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
        this.apiMapper = apiMapper;
    }

    @GetMapping
    public List<CategoryResponseDTO> getAllCategories() {
        return listCategoriesUseCase.getAllCategories().stream()
                .map(apiMapper::toResponse)
                .toList();
    }

    @GetMapping("/{categoryId}")
    public CategoryResponseDTO getCategory(@PathVariable UUID categoryId) {
        return apiMapper.toResponse(getCategoryUseCase.getCategoryById(categoryId));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO request) {
        CategoryResponseDTO created = apiMapper.toResponse(createCategoryUseCase.createCategory(apiMapper.toCommand(request)));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{categoryId}")
    public CategoryResponseDTO updateCategory(@PathVariable UUID categoryId, @RequestBody CategoryRequestDTO request) {
        return apiMapper.toResponse(updateCategoryUseCase.updateCategory(categoryId, apiMapper.toCommand(request)));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID categoryId) {
        deleteCategoryUseCase.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
