package com.hcmut.irms.menu_service.mapper;

import com.hcmut.irms.menu_service.dto.CategoryRequestDTO;
import com.hcmut.irms.menu_service.dto.CategoryResponseDTO;
import com.hcmut.irms.menu_service.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public void applyToCategory(Category category, CategoryRequestDTO request, boolean defaultActive) {
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setActive(request.getIsActive() == null ? defaultActive : request.getIsActive());
    }

    public CategoryResponseDTO toResponse(Category category) {
        CategoryResponseDTO response = new CategoryResponseDTO();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setActive(category.isActive());
        return response;
    }
}
