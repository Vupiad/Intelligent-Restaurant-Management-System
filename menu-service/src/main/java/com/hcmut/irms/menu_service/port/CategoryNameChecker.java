package com.hcmut.irms.menu_service.port;

import java.util.UUID;

public interface CategoryNameChecker {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID categoryId);
}
