package com.hcmut.irms.menu_service.validation;

import com.hcmut.irms.menu_service.dto.MenuItemRequestDTO;
import com.hcmut.irms.menu_service.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class MenuItemRequestValidator {
    private final CategoryRepository categoryRepo;

    public MenuItemRequestValidator(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public void validate(MenuItemRequestDTO request) {
        if (request.getCategoryId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "categoryId is required");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        if (request.getBasePrice() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "basePrice is required");
        }
        if (request.getBasePrice().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "basePrice must be non-negative");
        }
        if (!categoryRepo.existsById(request.getCategoryId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found: " + request.getCategoryId());
        }
    }
}
