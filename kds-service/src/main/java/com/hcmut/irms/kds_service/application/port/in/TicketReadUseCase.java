package com.hcmut.irms.kds_service.application.port.in;

import com.hcmut.irms.kds_service.domain.model.KitchenTicket;

import java.util.List;

public interface TicketReadUseCase {
    List<KitchenTicket> getActiveTickets();
}
