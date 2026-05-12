package com.hcmut.irms.menu_service.port;

import com.hcmut.irms.menu_service.model.Promotion;

import java.util.List;

public interface PromotionCatalog {
    List<Promotion> findAll();

    List<Promotion> findActivePromotions();
}
