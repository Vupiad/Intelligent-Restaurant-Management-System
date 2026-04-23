package com.hcmut.irms.kds_service.domain.model;

import java.util.ArrayList;
import java.util.List;

public class TicketItem {
    private String menuItemId;
    private String itemName;
    private Integer quantity;
    private ItemStatus status;
    private List<String> customizations = new ArrayList<>();

    public TicketItem() {
    }

    public TicketItem(String menuItemId, String itemName, Integer quantity, ItemStatus status, List<String> customizations) {
        this.menuItemId = menuItemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.status = status;
        this.customizations = customizations == null ? new ArrayList<>() : new ArrayList<>(customizations);
    }

    public String getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(String menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public List<String> getCustomizations() {
        return customizations;
    }

    public void setCustomizations(List<String> customizations) {
        this.customizations = customizations == null ? new ArrayList<>() : new ArrayList<>(customizations);
    }
}
