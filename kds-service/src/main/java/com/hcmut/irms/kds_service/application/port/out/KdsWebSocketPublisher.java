package com.hcmut.irms.kds_service.application.port.out;

import com.hcmut.irms.kds_service.domain.model.KitchenTicket;

public interface KdsWebSocketPublisher {
    void broadcastNewTicket(KitchenTicket ticket);

    void broadcastTicketUpdate(KitchenTicket ticket);

    void broadcastTicketRemoval(String ticketId);
}
