package com.hcmut.irms.ordering_service.dto.external;

/**
 * Minimal projection of the menu-service {@code MenuItemResponseDTO}.
 * <p>
 * Jackson maps menu-service's boolean getter {@code isAvailable()} → JSON field "available".
 */
import java.util.UUID;

public class MenuItemResponse {
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
