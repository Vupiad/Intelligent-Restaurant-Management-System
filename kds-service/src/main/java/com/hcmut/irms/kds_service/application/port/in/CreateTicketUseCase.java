package com.hcmut.irms.kds_service.application.port.in;

import com.hcmut.irms.kds_service.application.command.CreateTicketCommand;

public interface CreateTicketUseCase {
    void createTicket(CreateTicketCommand command);
}
