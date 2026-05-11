package com.hcmut.irms.menu_service.validation;

import com.hcmut.irms.menu_service.dto.PromotionRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class PromotionRequestValidator {
    public void validate(PromotionRequestDTO request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        if (request.getType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type is required");
        }
        if (request.getDiscountValue() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "discountValue is required");
        }
        if (request.getDiscountValue().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "discountValue must be non-negative");
        }
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startTime and endTime are required");
        }
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endTime must be after startTime");
        }
    }
}
