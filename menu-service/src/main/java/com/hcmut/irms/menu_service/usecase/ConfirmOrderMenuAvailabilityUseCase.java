package com.hcmut.irms.menu_service.usecase;

import com.hcmut.irms.menu_service.service.OrderMenuAvailabilityCommand;

public interface ConfirmOrderMenuAvailabilityUseCase {
    void confirmAvailability(OrderMenuAvailabilityCommand command);
}
