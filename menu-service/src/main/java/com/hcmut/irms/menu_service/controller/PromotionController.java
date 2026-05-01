package com.hcmut.irms.menu_service.controller;

import com.hcmut.irms.menu_service.dto.PromotionRequestDTO;
import com.hcmut.irms.menu_service.dto.PromotionResponseDTO;
import com.hcmut.irms.menu_service.usecase.PromotionReadUseCase;
import com.hcmut.irms.menu_service.usecase.PromotionWriteUseCase;
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
    private final PromotionReadUseCase readUseCase;
    private final PromotionWriteUseCase writeUseCase;

    public PromotionController(PromotionReadUseCase readUseCase, PromotionWriteUseCase writeUseCase) {
        this.readUseCase = readUseCase;
        this.writeUseCase = writeUseCase;
    }

    @GetMapping
    public List<PromotionResponseDTO> getAllPromotions() {
        return readUseCase.getAllPromotions();
    }

    @GetMapping("/active")
    public List<PromotionResponseDTO> getActivePromotions() {
        return readUseCase.getActivePromotions();
    }

    @GetMapping("/{promotionId}")
    public PromotionResponseDTO getPromotion(@PathVariable UUID promotionId) {
        return readUseCase.getPromotionById(promotionId);
    }

    @PostMapping
    public ResponseEntity<PromotionResponseDTO> createPromotion(@RequestBody PromotionRequestDTO request) {
        PromotionResponseDTO created = writeUseCase.createPromotion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{promotionId}")
    public PromotionResponseDTO updatePromotion(@PathVariable UUID promotionId, @RequestBody PromotionRequestDTO request) {
        return writeUseCase.updatePromotion(promotionId, request);
    }

    @DeleteMapping("/{promotionId}")
    public ResponseEntity<Void> deletePromotion(@PathVariable UUID promotionId) {
        writeUseCase.deletePromotion(promotionId);
        return ResponseEntity.noContent().build();
    }
}
