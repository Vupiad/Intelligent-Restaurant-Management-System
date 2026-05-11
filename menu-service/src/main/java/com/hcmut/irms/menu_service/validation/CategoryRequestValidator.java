package com.hcmut.irms.menu_service.validation;

import com.hcmut.irms.menu_service.dto.CategoryRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CategoryRequestValidator {
    public void validate(CategoryRequestDTO request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
    }
}
