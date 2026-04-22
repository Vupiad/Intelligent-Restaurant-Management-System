package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.dto.CategoryResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryReadUseCase {
    List<CategoryResponseDTO> getAllCategories();

    CategoryResponseDTO getCategoryById(UUID categoryId);
}
