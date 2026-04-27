package com.hcmut.irms.ordering_service.port;

import com.hcmut.irms.ordering_service.dto.event.KdsOrderCreatedEvent;

public interface OrderEventPublisherPort {
    /**
     * Publishes an order-created event to the KDS via RabbitMQ.
     */
    void publishOrderCreated(KdsOrderCreatedEvent event);
}
