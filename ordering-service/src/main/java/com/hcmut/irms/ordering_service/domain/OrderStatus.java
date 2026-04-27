package com.hcmut.irms.ordering_service.domain;

public enum OrderStatus {
    CREATED,
    COOKING,
    READY,
    SERVED;

    /**
     * Defines valid forward transitions.
     * CREATED → COOKING → READY → SERVED
     * CREATED → READY is also valid because KDS only emits READY events.
     */
    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case CREATED -> next == COOKING || next == READY;
            case COOKING -> next == READY;
            case READY   -> next == SERVED;
            case SERVED  -> false;
        };
    }

    public static OrderStatus fromString(String value) {
        try {
            return OrderStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown order status: " + value);
        }
    }
}
