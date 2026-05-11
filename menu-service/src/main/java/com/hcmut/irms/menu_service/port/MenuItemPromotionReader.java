package com.hcmut.irms.menu_service.port;

import com.hcmut.irms.menu_service.model.MenuItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuItemPromotionReader {
    List<MenuItem> findAllWithPromotions();

    List<MenuItem> findAvailableWithPromotions();

    Optional<MenuItem> findByIdWithPromotions(UUID menuItemId);
}
