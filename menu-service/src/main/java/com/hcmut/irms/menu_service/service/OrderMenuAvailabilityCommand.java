package com.hcmut.irms.menu_service.service;

import java.util.List;

public record OrderMenuAvailabilityCommand(String orderId, List<String> menuItemIds) {
}
