package com.hcmut.irms.menu_service.controller;

import com.hcmut.irms.menu_service.dto.PromotionRequestDTO;
import com.hcmut.irms.menu_service.dto.PromotionResponseDTO;
import com.hcmut.irms.menu_service.mapper.MenuApiMapper;
import com.hcmut.irms.menu_service.usecase.CreatePromotionUseCase;
import com.hcmut.irms.menu_service.usecase.DeletePromotionUseCase;
import com.hcmut.irms.menu_service.usecase.GetPromotionUseCase;
import com.hcmut.irms.menu_service.usecase.ListActivePromotionsUseCase;
import com.hcmut.irms.menu_service.usecase.ListPromotionsUseCase;
import com.hcmut.irms.menu_service.usecase.UpdatePromotionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/menu/promotions")
public class PromotionController {
    private final ListPromotionsUseCase listPromotionsUseCase;
    private final ListActivePromotionsUseCase listActivePromotionsUseCase;
    private final GetPromotionUseCase getPromotionUseCase;
    private final CreatePromotionUseCase createPromotionUseCase;
    private final UpdatePromotionUseCase updatePromotionUseCase;
    private final DeletePromotionUseCase deletePromotionUseCase;
    private final MenuApiMapper apiMapper;

    public PromotionController(ListPromotionsUseCase listPromotionsUseCase,
                               ListActivePromotionsUseCase listActivePromotionsUseCase,
                               GetPromotionUseCase getPromotionUseCase,
                               CreatePromotionUseCase createPromotionUseCase,
                               UpdatePromotionUseCase updatePromotionUseCase,
                               DeletePromotionUseCase deletePromotionUseCase,
                               MenuApiMapper apiMapper) {
        this.listPromotionsUseCase = listPromotionsUseCase;
        this.listActivePromotionsUseCase = listActivePromotionsUseCase;
        this.getPromotionUseCase = getPromotionUseCase;
        this.createPromotionUseCase = createPromotionUseCase;
        this.updatePromotionUseCase = updatePromotionUseCase;
        this.deletePromotionUseCase = deletePromotionUseCase;
        this.apiMapper = apiMapper;
    }

    @GetMapping
    public List<PromotionResponseDTO> getAllPromotions() {
        return listPromotionsUseCase.getAllPromotions().stream()
                .map(apiMapper::toResponse)
                .toList();
    }

    @GetMapping("/active")
    public List<PromotionResponseDTO> getActivePromotions() {
        return listActivePromotionsUseCase.getActivePromotions().stream()
                .map(apiMapper::toResponse)
                .toList();
    }

    @GetMapping("/{promotionId}")
    public PromotionResponseDTO getPromotion(@PathVariable UUID promotionId) {
        return apiMapper.toResponse(getPromotionUseCase.getPromotionById(promotionId));
    }

    @PostMapping
    public ResponseEntity<PromotionResponseDTO> createPromotion(@RequestBody PromotionRequestDTO request) {
        PromotionResponseDTO created = apiMapper.toResponse(createPromotionUseCase.createPromotion(apiMapper.toCommand(request)));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{promotionId}")
    public PromotionResponseDTO updatePromotion(@PathVariable UUID promotionId, @RequestBody PromotionRequestDTO request) {
        return apiMapper.toResponse(updatePromotionUseCase.updatePromotion(promotionId, apiMapper.toCommand(request)));
    }

    @DeleteMapping("/{promotionId}")
    public ResponseEntity<Void> deletePromotion(@PathVariable UUID promotionId) {
        deletePromotionUseCase.deletePromotion(promotionId);
        return ResponseEntity.noContent().build();
    }
}
