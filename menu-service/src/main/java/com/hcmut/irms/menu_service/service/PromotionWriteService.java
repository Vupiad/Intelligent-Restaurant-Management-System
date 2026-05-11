package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.dto.PromotionRequestDTO;
import com.hcmut.irms.menu_service.dto.PromotionResponseDTO;
import com.hcmut.irms.menu_service.mapper.PromotionMapper;
import com.hcmut.irms.menu_service.model.MenuItem;
import com.hcmut.irms.menu_service.model.Promotion;
import com.hcmut.irms.menu_service.repository.MenuItemRepository;
import com.hcmut.irms.menu_service.repository.PromotionRepository;
import com.hcmut.irms.menu_service.usecase.PromotionWriteUseCase;
import com.hcmut.irms.menu_service.validation.PromotionRequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class PromotionWriteService implements PromotionWriteUseCase {
    private final MenuItemRepository itemRepo;
    private final PromotionRepository promoRepo;
    private final PromotionRequestValidator requestValidator;
    private final PromotionMapper promotionMapper;

    public PromotionWriteService(MenuItemRepository itemRepo,
                                 PromotionRepository promoRepo,
                                 PromotionRequestValidator requestValidator,
                                 PromotionMapper promotionMapper) {
        this.itemRepo = itemRepo;
        this.promoRepo = promoRepo;
        this.requestValidator = requestValidator;
        this.promotionMapper = promotionMapper;
    }

    @Override
    @Transactional
    public PromotionResponseDTO createPromotion(PromotionRequestDTO request) {
        requestValidator.validate(request);
        if (promoRepo.findByName(request.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Promotion name already exists: " + request.getName());
        }

        Promotion promotion = new Promotion();
        promotionMapper.applyToPromotion(promotion, request);
        promotion.setActive(true);

        Promotion saved = promoRepo.save(promotion);
        return promotionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PromotionResponseDTO updatePromotion(UUID promotionId, PromotionRequestDTO request) {
        requestValidator.validate(request);
        Promotion existing = promoRepo.findById(promotionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Promotion not found: " + promotionId));

        if (promoRepo.existsByNameAndIdNot(request.getName(), promotionId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Promotion name already exists: " + request.getName());
        }

        promotionMapper.applyToPromotion(existing, request);
        Promotion updated = promoRepo.save(existing);
        return promotionMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deletePromotion(UUID promotionId) {
        Promotion existing = promoRepo.findById(promotionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Promotion not found: " + promotionId));

        List<MenuItem> linkedItems = itemRepo.findByPromotions_Id(promotionId);
        for (MenuItem item : linkedItems) {
            item.getPromotions().removeIf(promotion -> promotion.getId().equals(promotionId));
        }
        itemRepo.saveAll(linkedItems);
        promoRepo.delete(existing);
    }

}
