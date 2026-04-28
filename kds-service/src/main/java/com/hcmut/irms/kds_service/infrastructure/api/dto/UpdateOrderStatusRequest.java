package com.hcmut.irms.kds_service.infrastructure.api.dto;

import com.hcmut.irms.kds_service.domain.model.TicketStatus;

public record UpdateOrderStatusRequest(TicketStatus status) {
}
