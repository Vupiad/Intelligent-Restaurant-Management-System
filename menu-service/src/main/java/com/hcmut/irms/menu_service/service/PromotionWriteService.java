package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.dto.PromotionRequestDTO;
import com.hcmut.irms.menu_service.dto.PromotionResponseDTO;
import com.hcmut.irms.menu_service.model.MenuItem;
import com.hcmut.irms.menu_service.model.Promotion;
import com.hcmut.irms.menu_service.repository.MenuItemRepository;
import com.hcmut.irms.menu_service.repository.PromotionRepository;
import com.hcmut.irms.menu_service.usecase.PromotionWriteUseCase;
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

    public PromotionWriteService(MenuItemRepository itemRepo, PromotionRepository promoRepo) {
        this.itemRepo = itemRepo;
        this.promoRepo = promoRepo;
    }

    @Override
    @Transactional
    public PromotionResponseDTO createPromotion(PromotionRequestDTO request) {
        validateRequest(request);
        if (promoRepo.findByName(request.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Promotion name already exists: " + request.getName());
        }

        Promotion promotion = new Promotion();
        applyRequestToPromotion(promotion, request);
        promotion.setActive(true);

        Promotion saved = promoRepo.save(promotion);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public PromotionResponseDTO updatePromotion(UUID promotionId, PromotionRequestDTO request) {
        validateRequest(request);
        Promotion existing = promoRepo.findById(promotionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Promotion not found: " + promotionId));

        if (promoRepo.existsByNameAndIdNot(request.getName(), promotionId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Promotion name already exists: " + request.getName());
        }

        applyRequestToPromotion(existing, request);
        Promotion updated = promoRepo.save(existing);
        return toResponse(updated);
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

    private void validateRequest(PromotionRequestDTO request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        if (request.getType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type is required");
        }
        if (request.getDiscountValue() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "discountValue is required");
        }
        if (request.getDiscountValue().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "discountValue must be non-negative");
        }
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startTime and endTime are required");
        }
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endTime must be after startTime");
        }
    }

    private void applyRequestToPromotion(Promotion promotion, PromotionRequestDTO request) {
        promotion.setName(request.getName());
        promotion.setType(request.getType());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setStartTime(request.getStartTime());
        promotion.setEndTime(request.getEndTime());
    }

    private PromotionResponseDTO toResponse(Promotion promotion) {
        PromotionResponseDTO response = new PromotionResponseDTO();
        response.setId(promotion.getId());
        response.setName(promotion.getName());
        response.setType(promotion.getType());
        response.setDiscountValue(promotion.getDiscountValue());
        response.setStartTime(promotion.getStartTime());
        response.setEndTime(promotion.getEndTime());
        response.setActive(promotion.isActive());
        return response;
    }
}
