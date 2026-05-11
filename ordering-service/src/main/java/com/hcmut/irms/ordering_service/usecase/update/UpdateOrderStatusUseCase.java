package com.hcmut.irms.ordering_service.usecase.update;

import com.hcmut.irms.ordering_service.domain.OrderStatus;

public interface UpdateOrderStatusUseCase {
    void updateStatus(Long orderId, OrderStatus newStatus);
}
