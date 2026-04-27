package com.hcmut.irms.ordering_service.usecase.create;

import com.hcmut.irms.ordering_service.dto.api.CreateOrderRequest;
import com.hcmut.irms.ordering_service.dto.api.OrderResponse;

public interface CreateOrderUseCase {
    OrderResponse createOrder(CreateOrderRequest request, String bearerToken);
}
