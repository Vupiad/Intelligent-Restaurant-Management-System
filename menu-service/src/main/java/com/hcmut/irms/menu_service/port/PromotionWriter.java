package com.hcmut.irms.menu_service.port;

import com.hcmut.irms.menu_service.model.Promotion;

public interface PromotionWriter {
    Promotion save(Promotion promotion);

    void delete(Promotion promotion);
}
