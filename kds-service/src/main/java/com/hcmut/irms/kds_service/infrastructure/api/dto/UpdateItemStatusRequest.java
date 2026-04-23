package com.hcmut.irms.kds_service.infrastructure.api.dto;

import com.hcmut.irms.kds_service.domain.model.ItemStatus;

public record UpdateItemStatusRequest(ItemStatus status) {
}
