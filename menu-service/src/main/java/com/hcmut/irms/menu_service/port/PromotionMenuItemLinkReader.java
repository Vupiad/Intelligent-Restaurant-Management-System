package com.hcmut.irms.menu_service.port;

import com.hcmut.irms.menu_service.model.MenuItem;

import java.util.List;
import java.util.UUID;

public interface PromotionMenuItemLinkReader {
    List<MenuItem> findByPromotions_Id(UUID promotionId);
}
