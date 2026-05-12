package com.hcmut.irms.kds_service.application.port.in;

public interface ConfirmMenuAvailabilityUseCase {
    void confirmMenuAvailability(String ticketId, boolean isAvailable);
}
