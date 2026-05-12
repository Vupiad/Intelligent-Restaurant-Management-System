package com.hcmut.irms.menu_service.mapper;

import com.hcmut.irms.menu_service.application.CategoryCommand;
import com.hcmut.irms.menu_service.application.CategoryView;
import com.hcmut.irms.menu_service.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public void applyToCategory(Category category, CategoryCommand command, boolean defaultActive) {
        category.setName(command.name());
        category.setDescription(command.description());
        category.setActive(command.active() == null ? defaultActive : command.active());
    }

    public CategoryView toView(Category category) {
        return new CategoryView(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isActive()
        );
    }
}
