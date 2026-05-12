package com.hcmut.irms.menu_service.port;

import com.hcmut.irms.menu_service.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryReader {
    List<Category> findAll();

    Optional<Category> findById(UUID categoryId);
}
