package com.hcmut.irms.ordering_service.usecase.create;

import com.hcmut.irms.ordering_service.domain.Order;
import com.hcmut.irms.ordering_service.domain.OrderItem;
import com.hcmut.irms.ordering_service.dto.api.CreateOrderRequest;
import com.hcmut.irms.ordering_service.dto.api.OrderResponse;
import com.hcmut.irms.ordering_service.dto.event.OrderCreatedEvent;
import com.hcmut.irms.ordering_service.mapper.OrderMapper;
import com.hcmut.irms.ordering_service.port.OrderEventPublisherPort;
import com.hcmut.irms.ordering_service.port.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final OrderEventPublisherPort orderEventPublisherPort;
    private final OrderMapper orderMapper;
    private final OrderCreatedEventFactory orderCreatedEventFactory;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String bearerToken) {

        List<OrderItem> orderItems = orderMapper.toOrderItems(request);

        Order order = Order.create(request.tableNumber(), request.staffName(), orderItems);

        Order saved = orderRepositoryPort.save(order);

        OrderCreatedEvent event = orderCreatedEventFactory.from(saved);

        orderEventPublisherPort.publishOrderCreated(event);

        return orderMapper.toResponse(saved);
    }
}
