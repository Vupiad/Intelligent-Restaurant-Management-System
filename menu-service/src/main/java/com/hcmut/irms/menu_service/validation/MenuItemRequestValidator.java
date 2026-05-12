package com.hcmut.irms.menu_service.validation;

import com.hcmut.irms.menu_service.application.MenuItemCommand;
import com.hcmut.irms.menu_service.exception.MenuBadRequestException;
import com.hcmut.irms.menu_service.exception.MenuNotFoundException;
import com.hcmut.irms.menu_service.port.CategoryExistenceChecker;
import org.springframework.stereotype.Component;

@Component
public class MenuItemRequestValidator {
    private final CategoryExistenceChecker categoryExistenceChecker;

    public MenuItemRequestValidator(CategoryExistenceChecker categoryExistenceChecker) {
        this.categoryExistenceChecker = categoryExistenceChecker;
    }

    public void validate(MenuItemCommand command) {
        if (command.categoryId() == null) {
            throw new MenuBadRequestException("categoryId is required");
        }
        if (command.name() == null || command.name().isBlank()) {
            throw new MenuBadRequestException("name is required");
        }
        if (command.basePrice() == null) {
            throw new MenuBadRequestException("basePrice is required");
        }
        if (command.basePrice().signum() < 0) {
            throw new MenuBadRequestException("basePrice must be non-negative");
        }
        if (!categoryExistenceChecker.existsById(command.categoryId())) {
            throw new MenuNotFoundException("Category not found: " + command.categoryId());
        }
    }
}
