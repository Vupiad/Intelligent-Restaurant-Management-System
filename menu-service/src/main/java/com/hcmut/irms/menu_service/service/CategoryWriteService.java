package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.dto.CategoryRequestDTO;
import com.hcmut.irms.menu_service.dto.CategoryResponseDTO;
import com.hcmut.irms.menu_service.model.Category;
import com.hcmut.irms.menu_service.repository.CategoryRepository;
import com.hcmut.irms.menu_service.repository.MenuItemRepository;
import com.hcmut.irms.menu_service.usecase.CategoryWriteUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class CategoryWriteService implements CategoryWriteUseCase {
    private final CategoryRepository categoryRepo;
    private final MenuItemRepository itemRepo;

    public CategoryWriteService(CategoryRepository categoryRepo, MenuItemRepository itemRepo) {
        this.categoryRepo = categoryRepo;
        this.itemRepo = itemRepo;
    }

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        validateRequest(request);
        if (categoryRepo.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists: " + request.getName());
        }

        Category category = new Category();
        applyRequestToCategory(category, request, true);
        Category saved = categoryRepo.save(category);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(UUID categoryId, CategoryRequestDTO request) {
        validateRequest(request);
        Category existing = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found: " + categoryId));

        if (categoryRepo.existsByNameAndIdNot(request.getName(), categoryId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists: " + request.getName());
        }

        applyRequestToCategory(existing, request, existing.isActive());
        Category updated = categoryRepo.save(existing);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID categoryId) {
        Category existing = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found: " + categoryId));

        if (itemRepo.existsByCategory_Id(categoryId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category is being used by menu items: " + categoryId);
        }
        categoryRepo.delete(existing);
    }

    private void validateRequest(CategoryRequestDTO request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
    }

    private void applyRequestToCategory(Category category, CategoryRequestDTO request, boolean defaultActive) {
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setActive(request.getIsActive() == null ? defaultActive : request.getIsActive());
    }

    private CategoryResponseDTO toResponse(Category category) {
        CategoryResponseDTO response = new CategoryResponseDTO();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setActive(category.isActive());
        return response;
    }
}
