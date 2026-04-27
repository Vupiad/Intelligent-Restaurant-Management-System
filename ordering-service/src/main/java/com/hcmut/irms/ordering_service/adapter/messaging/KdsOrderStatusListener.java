package com.hcmut.irms.ordering_service.adapter.messaging;

import com.hcmut.irms.ordering_service.dto.event.KdsStatusEvent;
import com.hcmut.irms.ordering_service.usecase.update.UpdateOrderStatusUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes status-update events published by kds-service.
 * <p>
 * Contains NO business logic — simply delegates to {@link UpdateOrderStatusUseCase}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KdsOrderStatusListener {

    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

    @RabbitListener(queues = "${app.rabbitmq.order-status-queue:ordering.kds.status}")
    public void onKdsStatusEvent(KdsStatusEvent event) {
        log.info("Received KDS status event: orderId={} newStatus={} updatedBy={}",
                event.orderId(), event.newStatus(), event.updatedBy());
        updateOrderStatusUseCase.updateStatus(event.orderId(), event.newStatus());
    }
}
