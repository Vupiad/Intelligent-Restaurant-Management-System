package com.hcmut.irms.ordering_service.usecase.get;

import com.hcmut.irms.ordering_service.dto.api.OrderResponse;

public interface GetOrderUseCase {
    OrderResponse getOrder(Long orderId);
}
