package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.application.CategoryCommand;
import com.hcmut.irms.menu_service.application.CategoryView;

import java.util.UUID;

public interface UpdateCategoryUseCase {
    CategoryView updateCategory(UUID categoryId, CategoryCommand command);
}
