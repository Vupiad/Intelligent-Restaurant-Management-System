package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.application.CategoryView;

import java.util.List;

public interface ListCategoriesUseCase {
    List<CategoryView> getAllCategories();
}
