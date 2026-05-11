package com.hcmut.irms.menu_service.validation;

import com.hcmut.irms.menu_service.application.PromotionCommand;
import com.hcmut.irms.menu_service.exception.MenuBadRequestException;
import org.springframework.stereotype.Component;

@Component
public class PromotionRequestValidator {
    public void validate(PromotionCommand command) {
        if (command.name() == null || command.name().isBlank()) {
            throw new MenuBadRequestException("name is required");
        }
        if (command.type() == null) {
            throw new MenuBadRequestException("type is required");
        }
        if (command.discountValue() == null) {
            throw new MenuBadRequestException("discountValue is required");
        }
        if (command.discountValue().signum() < 0) {
            throw new MenuBadRequestException("discountValue must be non-negative");
        }
        if (command.startTime() == null || command.endTime() == null) {
            throw new MenuBadRequestException("startTime and endTime are required");
        }
        if (!command.endTime().isAfter(command.startTime())) {
            throw new MenuBadRequestException("endTime must be after startTime");
        }
    }
}
