package com.hcmut.irms.kds_service.application.service;

import com.hcmut.irms.kds_service.application.exception.TicketNotFoundException;
import com.hcmut.irms.kds_service.application.mapper.KitchenTicketMapper;
import com.hcmut.irms.kds_service.application.port.in.TicketWriteUseCase;
import com.hcmut.irms.kds_service.application.port.out.KdsWebSocketPublisher;
import com.hcmut.irms.kds_service.application.port.out.OrderStatusPublisher;
import com.hcmut.irms.kds_service.domain.model.KitchenTicket;
import com.hcmut.irms.kds_service.domain.model.TicketStatus;
import com.hcmut.irms.kds_service.domain.repository.KitchenTicketRepository;
import com.hcmut.irms.kds_service.infrastructure.messaging.event.OrderCreatedEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TicketWriteService implements TicketWriteUseCase {
    private final KitchenTicketRepository repository;
    private final KdsWebSocketPublisher webSocketPublisher;
    private final OrderStatusPublisher orderStatusPublisher;
    private final KitchenTicketMapper ticketMapper;
    private final TicketStatusPolicy statusPolicy;

    public TicketWriteService(KitchenTicketRepository repository, KdsWebSocketPublisher webSocketPublisher,
                              OrderStatusPublisher orderStatusPublisher, KitchenTicketMapper ticketMapper,
                              TicketStatusPolicy statusPolicy) {
        this.repository = repository;
        this.webSocketPublisher = webSocketPublisher;
        this.orderStatusPublisher = orderStatusPublisher;
        this.ticketMapper = ticketMapper;
        this.statusPolicy = statusPolicy;
    }

    @Override
    public void createTicketFromEvent(OrderCreatedEvent event) {
        KitchenTicket saved = repository.save(ticketMapper.from(event));
        webSocketPublisher.broadcastNewTicket(saved);
    }

    @Override
    public void confirmMenuAvailability(String ticketId, boolean isAvailable) {
        KitchenTicket ticket = repository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException(ticketId));
        ticket.setStatus(isAvailable ? TicketStatus.KITCHEN_PENDING : TicketStatus.REJECT);
        ticket.setCompletedAt(isAvailable ? null : LocalDateTime.now(ZoneOffset.UTC));

        KitchenTicket saved = repository.save(ticket);
        if (isAvailable) {
            webSocketPublisher.broadcastTicketUpdate(saved);
            return;
        }
        webSocketPublisher.broadcastTicketRemoval(saved.getId());
    }




    @Override
    public void updateOrderStatus(String ticketId, TicketStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }
        if (!statusPolicy.canBeUpdatedByKitchen(status)) {
            throw new IllegalArgumentException("Status must be COOKING, READY, or SERVED");
        }

        KitchenTicket ticket = repository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException(ticketId));
        ticket.setStatus(status);
        ticket.setCompletedAt(status == TicketStatus.COOKING ? null : LocalDateTime.now(ZoneOffset.UTC));

        KitchenTicket saved = repository.save(ticket);
        orderStatusPublisher.publishOrderStatusEvent(saved.getId(), status);
        if (statusPolicy.shouldStayOnActiveBoard(status)) {
            webSocketPublisher.broadcastTicketUpdate(saved);
            return;
        }
        webSocketPublisher.broadcastTicketRemoval(saved.getId());
    }

}
