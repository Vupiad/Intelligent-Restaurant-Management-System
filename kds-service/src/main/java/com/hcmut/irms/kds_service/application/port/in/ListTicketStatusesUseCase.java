package com.hcmut.irms.kds_service.application.port.in;

import com.hcmut.irms.kds_service.domain.model.TicketStatus;

import java.util.List;

public interface ListTicketStatusesUseCase {
    List<TicketStatus> getTicketStatuses();
}
