package com.hcmut.irms.ordering_service.usecase.update;

import com.hcmut.irms.ordering_service.domain.Order;
import com.hcmut.irms.ordering_service.domain.OrderStatus;
import com.hcmut.irms.ordering_service.domain.exception.OrderNotFoundException;
import com.hcmut.irms.ordering_service.port.OrderRepositoryPort;
import com.hcmut.irms.ordering_service.port.OrderStatusNotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateOrderStatusService implements UpdateOrderStatusUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final OrderStatusNotificationPort orderStatusNotificationPort;

    @Override
    @Transactional
    public void updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Domain enforces the valid transition rule
        order.applyStatusTransition(newStatus);

        orderRepositoryPort.save(order);
        orderStatusNotificationPort.publish(String.valueOf(orderId), newStatus.name());
        log.info("Order {} status updated to {}", orderId, newStatus);
    }
}
