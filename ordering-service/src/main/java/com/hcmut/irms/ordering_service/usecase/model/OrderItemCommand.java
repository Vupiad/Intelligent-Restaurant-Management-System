package com.hcmut.irms.ordering_service.usecase.model;

import java.util.List;

public record OrderItemCommand(
        String menuItemId,
        String name,
        Integer quantity,
        List<String> customizations,
        List<String> notes
) {
}
