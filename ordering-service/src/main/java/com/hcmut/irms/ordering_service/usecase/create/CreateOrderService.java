package com.hcmut.irms.ordering_service.usecase.create;

import com.hcmut.irms.ordering_service.domain.Order;
import com.hcmut.irms.ordering_service.domain.OrderItem;
import com.hcmut.irms.ordering_service.dto.event.OrderCreatedEvent;
import com.hcmut.irms.ordering_service.mapper.OrderMapper;
import com.hcmut.irms.ordering_service.port.OrderEventPublisherPort;
import com.hcmut.irms.ordering_service.port.OrderRepositoryPort;
import com.hcmut.irms.ordering_service.usecase.model.CreateOrderCommand;
import com.hcmut.irms.ordering_service.usecase.model.OrderResult;
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
    public OrderResult createOrder(CreateOrderCommand command) {

        List<OrderItem> orderItems = orderMapper.toOrderItems(command);

        Order order = Order.create(command.tableNumber(), command.staffName(), orderItems);

        Order saved = orderRepositoryPort.save(order);

        OrderCreatedEvent event = orderCreatedEventFactory.from(saved);

        orderEventPublisherPort.publishOrderCreated(event);

        return orderMapper.toResult(saved);
    }
}
