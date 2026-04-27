package com.hcmut.irms.ordering_service.adapter.persistence;

import com.hcmut.irms.ordering_service.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
}
