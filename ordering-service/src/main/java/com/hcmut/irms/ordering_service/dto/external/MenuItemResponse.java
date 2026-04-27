package com.hcmut.irms.ordering_service.dto.external;

/**
 * Minimal projection of the menu-service {@code MenuItemResponseDTO}.
 * <p>
 * Jackson maps menu-service's boolean getter {@code isAvailable()} → JSON field "available".
 */
public class MenuItemResponse {

    private String id;
    private String name;
    private boolean available;   // JSON key: "available" (from isAvailable() getter in menu-service)

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
