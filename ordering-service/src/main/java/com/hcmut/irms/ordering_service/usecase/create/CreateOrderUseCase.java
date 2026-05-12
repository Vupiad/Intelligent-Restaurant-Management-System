package com.hcmut.irms.ordering_service.usecase.create;

import com.hcmut.irms.ordering_service.usecase.model.CreateOrderCommand;
import com.hcmut.irms.ordering_service.usecase.model.OrderResult;

public interface CreateOrderUseCase {
    OrderResult createOrder(CreateOrderCommand command);
}
