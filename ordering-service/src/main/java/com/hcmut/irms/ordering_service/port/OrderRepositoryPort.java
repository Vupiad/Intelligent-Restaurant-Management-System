package com.hcmut.irms.ordering_service.port;

import com.hcmut.irms.ordering_service.domain.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findAll();
}
