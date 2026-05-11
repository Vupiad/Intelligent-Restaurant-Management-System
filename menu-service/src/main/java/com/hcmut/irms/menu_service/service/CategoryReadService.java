package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.application.CategoryView;
import com.hcmut.irms.menu_service.exception.MenuNotFoundException;
import com.hcmut.irms.menu_service.mapper.CategoryMapper;
import com.hcmut.irms.menu_service.model.Category;
import com.hcmut.irms.menu_service.port.CategoryReader;
import com.hcmut.irms.menu_service.usecase.GetCategoryUseCase;
import com.hcmut.irms.menu_service.usecase.ListCategoriesUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryReadService implements ListCategoriesUseCase, GetCategoryUseCase {
    private final CategoryReader categoryReader;
    private final CategoryMapper categoryMapper;

    public CategoryReadService(CategoryReader categoryReader, CategoryMapper categoryMapper) {
        this.categoryReader = categoryReader;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryView> getAllCategories() {
        return categoryReader.findAll().stream()
                .map(categoryMapper::toView)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryView getCategoryById(UUID categoryId) {
        Category category = categoryReader.findById(categoryId)
                .orElseThrow(() -> new MenuNotFoundException("Category not found: " + categoryId));
        return categoryMapper.toView(category);
    }
}
