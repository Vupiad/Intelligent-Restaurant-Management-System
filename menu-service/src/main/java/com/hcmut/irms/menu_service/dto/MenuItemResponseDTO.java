package com.hcmut.irms.menu_service.dto;

import com.hcmut.irms.menu_service.model.Customization;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuItemResponseDTO {
    private UUID id;
    private UUID categoryId;
    private String name;
    private String description;
    private BigDecimal originalPrice;
    private BigDecimal finalCalculatedPrice;
    private boolean isAvailable;
    private String imageUrl;
    private List<Customization> customizations;
    private List<String> activePromotions;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getFinalCalculatedPrice() {
        return finalCalculatedPrice;
    }

    public void setFinalCalculatedPrice(BigDecimal finalCalculatedPrice) {
        this.finalCalculatedPrice = finalCalculatedPrice;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Customization> getCustomizations() {
        return customizations;
    }

    public void setCustomizations(List<Customization> customizations) {
        this.customizations = customizations;
    }

    public List<String> getActivePromotions() {
        return activePromotions;
    }

    public void setActivePromotions(List<String> activePromotions) {
        this.activePromotions = activePromotions;
    }
}
