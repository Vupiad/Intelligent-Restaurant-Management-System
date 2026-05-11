package com.hcmut.irms.menu_service.port;

import com.hcmut.irms.menu_service.model.Category;

import java.util.UUID;

public interface CategoryReferenceProvider {
    Category getReferenceById(UUID categoryId);
}
