package com.hcmut.irms.menu_service.application;

import java.util.UUID;

public record CategoryView(
        UUID id,
        String name,
        String description,
        boolean active
) {
}
