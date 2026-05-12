package com.hcmut.irms.menu_service.port;

import java.util.UUID;

public interface CategoryExistenceChecker {
    boolean existsById(UUID categoryId);
}
