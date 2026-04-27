package com.hcmut.irms.ordering_service.domain.exception;

import com.hcmut.irms.ordering_service.domain.OrderStatus;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(OrderStatus current, OrderStatus attempted) {
        super("Invalid status transition: " + current + " → " + attempted);
    }
}
