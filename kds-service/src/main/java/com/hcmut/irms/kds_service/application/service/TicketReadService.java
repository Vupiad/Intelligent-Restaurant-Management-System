package com.hcmut.irms.kds_service.application.service;

import com.hcmut.irms.kds_service.application.port.in.TicketReadUseCase;
import com.hcmut.irms.kds_service.domain.model.KitchenTicket;
import com.hcmut.irms.kds_service.domain.model.TicketStatus;
import com.hcmut.irms.kds_service.domain.repository.KitchenTicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketReadService implements TicketReadUseCase {
    private final KitchenTicketRepository repository;

    public TicketReadService(KitchenTicketRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<KitchenTicket> getActiveTickets() {
        return repository.findByStatusNotIn(List.of(TicketStatus.READY, TicketStatus.SERVED));
    }
}
