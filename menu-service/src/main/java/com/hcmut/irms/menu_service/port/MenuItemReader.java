package com.hcmut.irms.menu_service.port;

import com.hcmut.irms.menu_service.model.MenuItem;

import java.util.Optional;
import java.util.UUID;

public interface MenuItemReader {
    Optional<MenuItem> findById(UUID menuItemId);
}
