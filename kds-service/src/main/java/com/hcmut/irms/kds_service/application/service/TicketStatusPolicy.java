package com.hcmut.irms.kds_service.application.service;

import com.hcmut.irms.kds_service.domain.model.TicketStatus;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;

@Component
public class TicketStatusPolicy {
    private static final EnumSet<TicketStatus> KITCHEN_UPDATE_STATUSES =
            EnumSet.of(TicketStatus.COOKING, TicketStatus.READY, TicketStatus.SERVED);

    public boolean canBeUpdatedByKitchen(TicketStatus status) {
        return KITCHEN_UPDATE_STATUSES.contains(status);
    }

    public boolean shouldStayOnActiveBoard(TicketStatus status) {
        return status == TicketStatus.COOKING;
    }

    public List<TicketStatus> inactiveStatuses() {
        return List.of(
                TicketStatus.WAIT_FOR_MENU_CONFIRM,
                TicketStatus.REJECT,
                TicketStatus.READY,
                TicketStatus.SERVED
        );
    }
}
