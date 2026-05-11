package com.hcmut.irms.ordering_service.usecase.get;

import com.hcmut.irms.ordering_service.domain.Order;
import com.hcmut.irms.ordering_service.domain.exception.OrderNotFoundException;
import com.hcmut.irms.ordering_service.mapper.OrderMapper;
import com.hcmut.irms.ordering_service.port.OrderRepositoryPort;
import com.hcmut.irms.ordering_service.usecase.model.OrderResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetOrderService implements GetOrderByIdUseCase, ListOrdersUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public OrderResult getOrder(Long orderId) {
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return orderMapper.toResult(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResult> getAllOrders() {
        return orderRepositoryPort.findAll().stream()
                .map(orderMapper::toResult)
                .toList();
    }
}
