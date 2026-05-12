package com.hcmut.irms.kds_service.application.service;

import com.hcmut.irms.kds_service.application.command.CreateTicketCommand;
import com.hcmut.irms.kds_service.application.exception.TicketNotFoundException;
import com.hcmut.irms.kds_service.application.mapper.KitchenTicketMapper;
import com.hcmut.irms.kds_service.application.port.in.ConfirmMenuAvailabilityUseCase;
import com.hcmut.irms.kds_service.application.port.in.CreateTicketUseCase;
import com.hcmut.irms.kds_service.application.port.in.UpdateTicketStatusUseCase;
import com.hcmut.irms.kds_service.application.port.out.KdsWebSocketPublisher;
import com.hcmut.irms.kds_service.application.port.out.KitchenTicketFinder;
import com.hcmut.irms.kds_service.application.port.out.KitchenTicketSaver;
import com.hcmut.irms.kds_service.application.port.out.OrderStatusPublisher;
import com.hcmut.irms.kds_service.domain.model.KitchenTicket;
import com.hcmut.irms.kds_service.domain.model.TicketStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TicketWriteService implements CreateTicketUseCase, ConfirmMenuAvailabilityUseCase, UpdateTicketStatusUseCase {
    private final KitchenTicketSaver ticketSaver;
    private final KitchenTicketFinder ticketFinder;
    private final KdsWebSocketPublisher webSocketPublisher;
    private final OrderStatusPublisher orderStatusPublisher;
    private final KitchenTicketMapper ticketMapper;
    private final TicketStatusPolicy statusPolicy;

    public TicketWriteService(KitchenTicketSaver ticketSaver, KitchenTicketFinder ticketFinder, KdsWebSocketPublisher webSocketPublisher,
                              OrderStatusPublisher orderStatusPublisher, KitchenTicketMapper ticketMapper,
                              TicketStatusPolicy statusPolicy) {
        this.ticketSaver = ticketSaver;
        this.ticketFinder = ticketFinder;
        this.webSocketPublisher = webSocketPublisher;
        this.orderStatusPublisher = orderStatusPublisher;
        this.ticketMapper = ticketMapper;
        this.statusPolicy = statusPolicy;
    }

    @Override
    public void createTicket(CreateTicketCommand command) {
        KitchenTicket saved = ticketSaver.save(ticketMapper.from(command));
        webSocketPublisher.broadcastNewTicket(saved);
    }

    @Override
    public void confirmMenuAvailability(String ticketId, boolean isAvailable) {
        KitchenTicket ticket = ticketFinder.findById(ticketId).orElseThrow(() -> new TicketNotFoundException(ticketId));
        ticket.setStatus(isAvailable ? TicketStatus.KITCHEN_PENDING : TicketStatus.REJECT);
        ticket.setCompletedAt(isAvailable ? null : LocalDateTime.now(ZoneOffset.UTC));

        KitchenTicket saved = ticketSaver.save(ticket);
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

        KitchenTicket ticket = ticketFinder.findById(ticketId).orElseThrow(() -> new TicketNotFoundException(ticketId));
        ticket.setStatus(status);
        ticket.setCompletedAt(status == TicketStatus.COOKING ? null : LocalDateTime.now(ZoneOffset.UTC));

        KitchenTicket saved = ticketSaver.save(ticket);
        orderStatusPublisher.publishOrderStatusEvent(saved.getId(), status);
        if (statusPolicy.shouldStayOnActiveBoard(status)) {
            webSocketPublisher.broadcastTicketUpdate(saved);
            return;
        }
        webSocketPublisher.broadcastTicketRemoval(saved.getId());
    }

}
