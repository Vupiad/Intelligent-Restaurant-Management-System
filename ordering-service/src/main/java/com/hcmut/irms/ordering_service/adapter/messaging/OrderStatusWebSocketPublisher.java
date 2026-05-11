package com.hcmut.irms.ordering_service.adapter.messaging;

import com.hcmut.irms.ordering_service.dto.api.OrderStatusRealtimeMessage;
import com.hcmut.irms.ordering_service.port.OrderStatusNotificationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStatusWebSocketPublisher implements OrderStatusNotificationPort {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void publish(String orderId, String status) {
        OrderStatusRealtimeMessage message = new OrderStatusRealtimeMessage(orderId, status);
        simpMessagingTemplate.convertAndSend("/topic/orders/status", message);
        simpMessagingTemplate.convertAndSend("/topic/orders/" + orderId + "/status", message);
    }
}
