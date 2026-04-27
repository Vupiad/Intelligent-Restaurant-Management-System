package com.hcmut.irms.ordering_service.usecase.update;

import com.hcmut.irms.ordering_service.domain.Order;
import com.hcmut.irms.ordering_service.domain.OrderStatus;
import com.hcmut.irms.ordering_service.domain.exception.OrderNotFoundException;
import com.hcmut.irms.ordering_service.port.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateOrderStatusService implements UpdateOrderStatusUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    @Override
    @Transactional
    public void updateStatus(String orderId, String newStatus) {
        Long id;
        try {
            id = Long.parseLong(orderId);
        } catch (NumberFormatException e) {
            log.warn("Received invalid orderId from KDS: '{}' — ignoring event", orderId);
            return;
        }

        Order order = orderRepositoryPort.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        OrderStatus targetStatus = OrderStatus.fromString(newStatus);

        // Domain enforces the valid transition rule
        order.applyStatusTransition(targetStatus);

        orderRepositoryPort.save(order);
        log.info("Order {} status updated to {}", id, targetStatus);
    }
}
