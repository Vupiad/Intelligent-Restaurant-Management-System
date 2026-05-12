package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.application.PromotionView;
import com.hcmut.irms.menu_service.exception.MenuNotFoundException;
import com.hcmut.irms.menu_service.mapper.PromotionMapper;
import com.hcmut.irms.menu_service.model.Promotion;
import com.hcmut.irms.menu_service.port.PromotionCatalog;
import com.hcmut.irms.menu_service.port.PromotionReader;
import com.hcmut.irms.menu_service.usecase.GetPromotionUseCase;
import com.hcmut.irms.menu_service.usecase.ListActivePromotionsUseCase;
import com.hcmut.irms.menu_service.usecase.ListPromotionsUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PromotionReadService implements ListPromotionsUseCase, ListActivePromotionsUseCase, GetPromotionUseCase {
    private final PromotionCatalog promotionCatalog;
    private final PromotionReader promotionReader;
    private final PromotionMapper promotionMapper;

    public PromotionReadService(PromotionCatalog promotionCatalog,
                                PromotionReader promotionReader,
                                PromotionMapper promotionMapper) {
        this.promotionCatalog = promotionCatalog;
        this.promotionReader = promotionReader;
        this.promotionMapper = promotionMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionView> getAllPromotions() {
        return promotionCatalog.findAll().stream()
                .map(promotionMapper::toView)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionView> getActivePromotions() {
        return promotionCatalog.findActivePromotions().stream()
                .map(promotionMapper::toView)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionView getPromotionById(UUID promotionId) {
        Promotion promotion = promotionReader.findById(promotionId)
                .orElseThrow(() -> new MenuNotFoundException("Promotion not found: " + promotionId));
        return promotionMapper.toView(promotion);
    }
}
