package com.hcmut.irms.menu_service.port;

import com.hcmut.irms.menu_service.model.Promotion;

import java.util.Optional;
import java.util.UUID;

public interface PromotionNameChecker {
    Optional<Promotion> findByName(String name);

    boolean existsByNameAndIdNot(String name, UUID promotionId);
}
