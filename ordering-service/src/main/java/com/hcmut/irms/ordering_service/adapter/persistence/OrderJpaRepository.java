package com.hcmut.irms.ordering_service.adapter.persistence;

import com.hcmut.irms.ordering_service.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

    @Override
    @EntityGraph(attributePaths = "items")
    List<Order> findAll();
}
