package com.hcmut.irms.menu_service.application;

public record CategoryCommand(
        String name,
        String description,
        Boolean active
) {
}
