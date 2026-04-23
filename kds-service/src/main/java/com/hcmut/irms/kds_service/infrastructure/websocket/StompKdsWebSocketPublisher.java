package com.hcmut.irms.kds_service.infrastructure.websocket;

import com.hcmut.irms.kds_service.application.port.out.KdsWebSocketPublisher;
import com.hcmut.irms.kds_service.domain.model.KitchenTicket;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class StompKdsWebSocketPublisher implements KdsWebSocketPublisher {
    public static final String NEW_TICKETS_TOPIC = "/topic/kitchen/new-tickets";
    public static final String TICKET_UPDATES_TOPIC = "/topic/kitchen/ticket-updates";
    public static final String COMPLETED_TICKETS_TOPIC = "/topic/kitchen/completed-tickets";

    private final SimpMessagingTemplate messagingTemplate;

    public StompKdsWebSocketPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void broadcastNewTicket(KitchenTicket ticket) {
        messagingTemplate.convertAndSend(NEW_TICKETS_TOPIC, ticket);
    }

    @Override
    public void broadcastTicketUpdate(KitchenTicket ticket) {
        messagingTemplate.convertAndSend(TICKET_UPDATES_TOPIC, ticket);
    }

    @Override
    public void broadcastTicketRemoval(String ticketId) {
        messagingTemplate.convertAndSend(COMPLETED_TICKETS_TOPIC, ticketId);
    }
}
