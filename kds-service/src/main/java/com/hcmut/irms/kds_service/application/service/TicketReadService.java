package com.hcmut.irms.kds_service.application.service;

import com.hcmut.irms.kds_service.application.port.in.GetActiveTicketsUseCase;
import com.hcmut.irms.kds_service.application.port.in.ListTicketStatusesUseCase;
import com.hcmut.irms.kds_service.application.port.out.ActiveKitchenTicketReader;
import com.hcmut.irms.kds_service.domain.model.KitchenTicket;
import com.hcmut.irms.kds_service.domain.model.TicketStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketReadService implements GetActiveTicketsUseCase, ListTicketStatusesUseCase {
    private final ActiveKitchenTicketReader activeTicketReader;
    private final TicketStatusPolicy statusPolicy;

    public TicketReadService(ActiveKitchenTicketReader activeTicketReader, TicketStatusPolicy statusPolicy) {
        this.activeTicketReader = activeTicketReader;
        this.statusPolicy = statusPolicy;
    }

    @Override
    public List<KitchenTicket> getActiveTickets() {
        return activeTicketReader.findByStatusNotIn(statusPolicy.inactiveStatuses());
    }

    @Override
    public List<TicketStatus> getTicketStatuses() {
        return List.of(TicketStatus.values());
    }
}
