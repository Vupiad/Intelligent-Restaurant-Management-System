package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.application.CategoryCommand;
import com.hcmut.irms.menu_service.application.CategoryView;
import com.hcmut.irms.menu_service.exception.MenuConflictException;
import com.hcmut.irms.menu_service.exception.MenuNotFoundException;
import com.hcmut.irms.menu_service.mapper.CategoryMapper;
import com.hcmut.irms.menu_service.model.Category;
import com.hcmut.irms.menu_service.port.CategoryNameChecker;
import com.hcmut.irms.menu_service.port.CategoryReader;
import com.hcmut.irms.menu_service.port.CategoryWriter;
import com.hcmut.irms.menu_service.port.MenuItemCategoryUsageChecker;
import com.hcmut.irms.menu_service.usecase.CreateCategoryUseCase;
import com.hcmut.irms.menu_service.usecase.DeleteCategoryUseCase;
import com.hcmut.irms.menu_service.usecase.UpdateCategoryUseCase;
import com.hcmut.irms.menu_service.validation.CategoryRequestValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CategoryWriteService implements CreateCategoryUseCase, UpdateCategoryUseCase, DeleteCategoryUseCase {
    private final CategoryReader categoryReader;
    private final CategoryNameChecker categoryNameChecker;
    private final CategoryWriter categoryWriter;
    private final MenuItemCategoryUsageChecker itemCategoryUsageChecker;
    private final CategoryRequestValidator requestValidator;
    private final CategoryMapper categoryMapper;

    public CategoryWriteService(CategoryReader categoryReader,
                                CategoryNameChecker categoryNameChecker,
                                CategoryWriter categoryWriter,
                                MenuItemCategoryUsageChecker itemCategoryUsageChecker,
                                CategoryRequestValidator requestValidator,
                                CategoryMapper categoryMapper) {
        this.categoryReader = categoryReader;
        this.categoryNameChecker = categoryNameChecker;
        this.categoryWriter = categoryWriter;
        this.itemCategoryUsageChecker = itemCategoryUsageChecker;
        this.requestValidator = requestValidator;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public CategoryView createCategory(CategoryCommand command) {
        requestValidator.validate(command);
        if (categoryNameChecker.existsByName(command.name())) {
            throw new MenuConflictException("Category name already exists: " + command.name());
        }

        Category category = new Category();
        categoryMapper.applyToCategory(category, command, true);
        Category saved = categoryWriter.save(category);
        return categoryMapper.toView(saved);
    }

    @Override
    @Transactional
    public CategoryView updateCategory(UUID categoryId, CategoryCommand command) {
        requestValidator.validate(command);
        Category existing = categoryReader.findById(categoryId)
                .orElseThrow(() -> new MenuNotFoundException("Category not found: " + categoryId));

        if (categoryNameChecker.existsByNameAndIdNot(command.name(), categoryId)) {
            throw new MenuConflictException("Category name already exists: " + command.name());
        }

        categoryMapper.applyToCategory(existing, command, existing.isActive());
        Category updated = categoryWriter.save(existing);
        return categoryMapper.toView(updated);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID categoryId) {
        Category existing = categoryReader.findById(categoryId)
                .orElseThrow(() -> new MenuNotFoundException("Category not found: " + categoryId));

        if (itemCategoryUsageChecker.existsByCategory_Id(categoryId)) {
            throw new MenuConflictException("Category is being used by menu items: " + categoryId);
        }
        categoryWriter.delete(existing);
    }

}
