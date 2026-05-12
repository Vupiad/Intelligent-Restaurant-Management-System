package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.application.CategoryView;

import java.util.UUID;

public interface GetCategoryUseCase {
    CategoryView getCategoryById(UUID categoryId);
}
