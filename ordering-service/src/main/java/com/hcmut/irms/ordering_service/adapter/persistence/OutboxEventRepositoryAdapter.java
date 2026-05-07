package com.hcmut.irms.ordering_service.adapter.persistence;

import com.hcmut.irms.ordering_service.domain.OutboxEvent;
import com.hcmut.irms.ordering_service.port.OutboxEventRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventRepositoryAdapter implements OutboxEventRepositoryPort {

    private final OutboxEventJpaRepository jpaRepository;

    @Override
    public OutboxEvent save(OutboxEvent outboxEvent) {
        return jpaRepository.save(outboxEvent);
    }
}
