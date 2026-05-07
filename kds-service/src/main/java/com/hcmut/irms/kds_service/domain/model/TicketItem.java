package com.hcmut.irms.kds_service.domain.model;

import java.util.ArrayList;
import java.util.List;

public class TicketItem {
    private String menuItemId;
    private String itemName;
    private Integer quantity;
    private List<String> customizations = new ArrayList<>();
    private List<String> notes = new ArrayList<>();
    public TicketItem() {
    }

    public TicketItem(String menuItemId, String itemName, Integer quantity, List<String> customizations, List<String> notes) {
        this.menuItemId = menuItemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.customizations = customizations == null ? new ArrayList<>() : new ArrayList<>(customizations);
        this.notes = notes == null ? new ArrayList<>() : new ArrayList<>(notes);
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

    public List<String> getCustomizations() {
        return customizations;
    }

    public void setCustomizations(List<String> customizations) {
        this.customizations = customizations == null ? new ArrayList<>() : new ArrayList<>(customizations);
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }
}
