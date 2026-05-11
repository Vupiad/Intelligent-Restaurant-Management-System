package com.hcmut.irms.kds_service.application.command;

import java.util.List;

public record CreateTicketCommand(
        String orderId,
        Integer tableNumber,
        String waiterId,
        String timestamp,
        List<Item> items
) {
    public record Item(
            String menuItemId,
            String itemName,
            Integer quantity,
            List<String> customizations,
            List<String> notes
    ) {
    }
}
