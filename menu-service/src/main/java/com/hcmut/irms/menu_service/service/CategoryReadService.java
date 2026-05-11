package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.dto.CategoryResponseDTO;
import com.hcmut.irms.menu_service.mapper.CategoryMapper;
import com.hcmut.irms.menu_service.model.Category;
import com.hcmut.irms.menu_service.repository.CategoryRepository;
import com.hcmut.irms.menu_service.usecase.CategoryReadUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryReadService implements CategoryReadUseCase {
    private final CategoryRepository categoryRepo;
    private final CategoryMapper categoryMapper;

    public CategoryReadService(CategoryRepository categoryRepo, CategoryMapper categoryMapper) {
        this.categoryRepo = categoryRepo;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepo.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(UUID categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found: " + categoryId));
        return categoryMapper.toResponse(category);
    }
}
