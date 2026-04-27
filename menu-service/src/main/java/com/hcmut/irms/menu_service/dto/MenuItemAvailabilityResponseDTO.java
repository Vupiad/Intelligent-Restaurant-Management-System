package com.hcmut.irms.menu_service.dto;

import java.util.UUID;

public class MenuItemAvailabilityResponseDTO {
    private UUID itemId;
    private boolean availableForOrder;

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public boolean isAvailableForOrder() {
        return availableForOrder;
    }

    public void setAvailableForOrder(boolean availableForOrder) {
        this.availableForOrder = availableForOrder;
    }
}
