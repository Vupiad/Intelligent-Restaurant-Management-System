package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.dto.PromotionResponseDTO;
import com.hcmut.irms.menu_service.model.Promotion;
import com.hcmut.irms.menu_service.repository.PromotionRepository;
import com.hcmut.irms.menu_service.usecase.PromotionReadUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class PromotionReadService implements PromotionReadUseCase {
    private final PromotionRepository promoRepo;

    public PromotionReadService(PromotionRepository promoRepo) {
        this.promoRepo = promoRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponseDTO> getAllPromotions() {
        return promoRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponseDTO> getActivePromotions() {
        return promoRepo.findActivePromotions().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionResponseDTO getPromotionById(UUID promotionId) {
        Promotion promotion = promoRepo.findById(promotionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Promotion not found: " + promotionId));
        return toResponse(promotion);
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
