package com.hcmut.irms.ordering_service.usecase.get;

import com.hcmut.irms.ordering_service.usecase.model.OrderResult;

import java.util.List;

public interface ListOrdersUseCase {
    List<OrderResult> getAllOrders();
}
