package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.dto.PromotionResponseDTO;
import com.hcmut.irms.menu_service.mapper.PromotionMapper;
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
    private final PromotionMapper promotionMapper;

    public PromotionReadService(PromotionRepository promoRepo, PromotionMapper promotionMapper) {
        this.promoRepo = promoRepo;
        this.promotionMapper = promotionMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponseDTO> getAllPromotions() {
        return promoRepo.findAll().stream()
                .map(promotionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponseDTO> getActivePromotions() {
        return promoRepo.findActivePromotions().stream()
                .map(promotionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionResponseDTO getPromotionById(UUID promotionId) {
        Promotion promotion = promoRepo.findById(promotionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Promotion not found: " + promotionId));
        return promotionMapper.toResponse(promotion);
    }
}
