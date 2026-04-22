package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.dto.CategoryRequestDTO;
import com.hcmut.irms.menu_service.dto.CategoryResponseDTO;

import java.util.UUID;

public interface CategoryWriteUseCase {
    CategoryResponseDTO createCategory(CategoryRequestDTO request);

    CategoryResponseDTO updateCategory(UUID categoryId, CategoryRequestDTO request);

    void deleteCategory(UUID categoryId);
}
