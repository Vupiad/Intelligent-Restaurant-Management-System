package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.application.PromotionCommand;
import com.hcmut.irms.menu_service.application.PromotionView;
import com.hcmut.irms.menu_service.exception.MenuConflictException;
import com.hcmut.irms.menu_service.exception.MenuNotFoundException;
import com.hcmut.irms.menu_service.mapper.PromotionMapper;
import com.hcmut.irms.menu_service.model.MenuItem;
import com.hcmut.irms.menu_service.model.Promotion;
import com.hcmut.irms.menu_service.port.MenuItemWriter;
import com.hcmut.irms.menu_service.port.PromotionMenuItemLinkReader;
import com.hcmut.irms.menu_service.port.PromotionNameChecker;
import com.hcmut.irms.menu_service.port.PromotionReader;
import com.hcmut.irms.menu_service.port.PromotionWriter;
import com.hcmut.irms.menu_service.usecase.CreatePromotionUseCase;
import com.hcmut.irms.menu_service.usecase.DeletePromotionUseCase;
import com.hcmut.irms.menu_service.usecase.UpdatePromotionUseCase;
import com.hcmut.irms.menu_service.validation.PromotionRequestValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PromotionWriteService implements CreatePromotionUseCase, UpdatePromotionUseCase, DeletePromotionUseCase {
    private final PromotionMenuItemLinkReader promotionMenuItemLinkReader;
    private final MenuItemWriter itemWriter;
    private final PromotionReader promotionReader;
    private final PromotionNameChecker promotionNameChecker;
    private final PromotionWriter promotionWriter;
    private final PromotionRequestValidator requestValidator;
    private final PromotionMapper promotionMapper;

    public PromotionWriteService(PromotionMenuItemLinkReader promotionMenuItemLinkReader,
                                 MenuItemWriter itemWriter,
                                 PromotionReader promotionReader,
                                 PromotionNameChecker promotionNameChecker,
                                 PromotionWriter promotionWriter,
                                 PromotionRequestValidator requestValidator,
                                 PromotionMapper promotionMapper) {
        this.promotionMenuItemLinkReader = promotionMenuItemLinkReader;
        this.itemWriter = itemWriter;
        this.promotionReader = promotionReader;
        this.promotionNameChecker = promotionNameChecker;
        this.promotionWriter = promotionWriter;
        this.requestValidator = requestValidator;
        this.promotionMapper = promotionMapper;
    }

    @Override
    @Transactional
    public PromotionView createPromotion(PromotionCommand command) {
        requestValidator.validate(command);
        if (promotionNameChecker.findByName(command.name()).isPresent()) {
            throw new MenuConflictException("Promotion name already exists: " + command.name());
        }

        Promotion promotion = new Promotion();
        promotionMapper.applyToPromotion(promotion, command);
        promotion.setActive(true);

        Promotion saved = promotionWriter.save(promotion);
        return promotionMapper.toView(saved);
    }

    @Override
    @Transactional
    public PromotionView updatePromotion(UUID promotionId, PromotionCommand command) {
        requestValidator.validate(command);
        Promotion existing = promotionReader.findById(promotionId)
                .orElseThrow(() -> new MenuNotFoundException("Promotion not found: " + promotionId));

        if (promotionNameChecker.existsByNameAndIdNot(command.name(), promotionId)) {
            throw new MenuConflictException("Promotion name already exists: " + command.name());
        }

        promotionMapper.applyToPromotion(existing, command);
        Promotion updated = promotionWriter.save(existing);
        return promotionMapper.toView(updated);
    }

    @Override
    @Transactional
    public void deletePromotion(UUID promotionId) {
        Promotion existing = promotionReader.findById(promotionId)
                .orElseThrow(() -> new MenuNotFoundException("Promotion not found: " + promotionId));

        List<MenuItem> linkedItems = promotionMenuItemLinkReader.findByPromotions_Id(promotionId);
        for (MenuItem item : linkedItems) {
            item.getPromotions().removeIf(promotion -> promotion.getId().equals(promotionId));
            itemWriter.save(item);
        }
        promotionWriter.delete(existing);
    }

}
