package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.application.CategoryCommand;
import com.hcmut.irms.menu_service.application.CategoryView;

public interface CreateCategoryUseCase {
    CategoryView createCategory(CategoryCommand command);
}
