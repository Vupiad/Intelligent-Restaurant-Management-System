package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.dto.CategoryRequestDTO;
import com.hcmut.irms.menu_service.dto.CategoryResponseDTO;
import com.hcmut.irms.menu_service.mapper.CategoryMapper;
import com.hcmut.irms.menu_service.model.Category;
import com.hcmut.irms.menu_service.repository.CategoryRepository;
import com.hcmut.irms.menu_service.repository.MenuItemRepository;
import com.hcmut.irms.menu_service.usecase.CategoryWriteUseCase;
import com.hcmut.irms.menu_service.validation.CategoryRequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class CategoryWriteService implements CategoryWriteUseCase {
    private final CategoryRepository categoryRepo;
    private final MenuItemRepository itemRepo;
    private final CategoryRequestValidator requestValidator;
    private final CategoryMapper categoryMapper;

    public CategoryWriteService(CategoryRepository categoryRepo,
                                MenuItemRepository itemRepo,
                                CategoryRequestValidator requestValidator,
                                CategoryMapper categoryMapper) {
        this.categoryRepo = categoryRepo;
        this.itemRepo = itemRepo;
        this.requestValidator = requestValidator;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        requestValidator.validate(request);
        if (categoryRepo.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists: " + request.getName());
        }

        Category category = new Category();
        categoryMapper.applyToCategory(category, request, true);
        Category saved = categoryRepo.save(category);
        return categoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(UUID categoryId, CategoryRequestDTO request) {
        requestValidator.validate(request);
        Category existing = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found: " + categoryId));

        if (categoryRepo.existsByNameAndIdNot(request.getName(), categoryId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category name already exists: " + request.getName());
        }

        categoryMapper.applyToCategory(existing, request, existing.isActive());
        Category updated = categoryRepo.save(existing);
        return categoryMapper.toResponse(updated);
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

}
