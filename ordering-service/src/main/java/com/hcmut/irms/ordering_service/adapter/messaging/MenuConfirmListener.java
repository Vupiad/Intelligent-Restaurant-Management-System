package com.hcmut.irms.ordering_service.adapter.messaging;

import com.hcmut.irms.ordering_service.domain.OrderStatus;
import com.hcmut.irms.ordering_service.dto.event.MenuConfirmEvent;
import com.hcmut.irms.ordering_service.usecase.update.UpdateOrderStatusUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuConfirmListener {

    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

    @RabbitListener(queues = "${app.rabbitmq.menu-confirm-queue:order.menu.confirm}")
    public void onMenuConfirm(MenuConfirmEvent event) {
        try {
            Long.parseLong(event.orderId());
        } catch (NumberFormatException e) {
            log.warn("Received invalid orderId from menu confirmation: '{}' — ignoring event", event.orderId());
            return;
        }

        OrderStatus targetStatus = event.isAvailable() ? OrderStatus.CONFIRM : OrderStatus.REJECT;
        String status = targetStatus.name();

        log.info("Received menu confirm event: orderId={} isAvailable={} -> status={}",
                event.orderId(), event.isAvailable(), status);

        updateOrderStatusUseCase.updateStatus(event.orderId(), status);
    }
}
