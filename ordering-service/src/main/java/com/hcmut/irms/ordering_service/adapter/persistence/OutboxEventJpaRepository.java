package com.hcmut.irms.ordering_service.adapter.persistence;

import com.hcmut.irms.ordering_service.domain.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEvent, UUID> {
}
