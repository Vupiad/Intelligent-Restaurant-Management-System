package com.hcmut.irms.menu_service.mapper;

import com.hcmut.irms.menu_service.application.PromotionCommand;
import com.hcmut.irms.menu_service.application.PromotionView;
import com.hcmut.irms.menu_service.model.Promotion;
import org.springframework.stereotype.Component;

@Component
public class PromotionMapper {
    public void applyToPromotion(Promotion promotion, PromotionCommand command) {
        promotion.setName(command.name());
        promotion.setType(command.type());
        promotion.setDiscountValue(command.discountValue());
        promotion.setStartTime(command.startTime());
        promotion.setEndTime(command.endTime());
    }

    public PromotionView toView(Promotion promotion) {
        return new PromotionView(
                promotion.getId(),
                promotion.getName(),
                promotion.getType(),
                promotion.getDiscountValue(),
                promotion.getStartTime(),
                promotion.getEndTime(),
                promotion.isActive()
        );
    }
}
