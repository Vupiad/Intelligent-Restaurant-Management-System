package com.hcmut.irms.ordering_service.usecase.get;

import com.hcmut.irms.ordering_service.usecase.model.OrderResult;

public interface GetOrderByIdUseCase {
    OrderResult getOrder(Long orderId);
}
