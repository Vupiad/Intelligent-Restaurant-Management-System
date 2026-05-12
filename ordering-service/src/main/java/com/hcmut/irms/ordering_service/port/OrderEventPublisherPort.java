package com.hcmut.irms.ordering_service.port;

import com.hcmut.irms.ordering_service.dto.event.OrderCreatedEvent;

public interface OrderEventPublisherPort {
    /**
     * Publishes an order-created event.
     */
    void publishOrderCreated(OrderCreatedEvent event);
}
