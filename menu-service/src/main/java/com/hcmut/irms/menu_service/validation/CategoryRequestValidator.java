package com.hcmut.irms.menu_service.validation;

import com.hcmut.irms.menu_service.application.CategoryCommand;
import com.hcmut.irms.menu_service.exception.MenuBadRequestException;
import org.springframework.stereotype.Component;

@Component
public class CategoryRequestValidator {
    public void validate(CategoryCommand command) {
        if (command.name() == null || command.name().isBlank()) {
            throw new MenuBadRequestException("name is required");
        }
    }
}
