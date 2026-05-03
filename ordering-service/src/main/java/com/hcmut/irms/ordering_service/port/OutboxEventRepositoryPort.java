package com.hcmut.irms.ordering_service.port;

import com.hcmut.irms.ordering_service.domain.OutboxEvent;

public interface OutboxEventRepositoryPort {
    OutboxEvent save(OutboxEvent outboxEvent);
}
