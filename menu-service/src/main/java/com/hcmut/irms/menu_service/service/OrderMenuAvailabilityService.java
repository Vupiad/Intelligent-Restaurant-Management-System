package com.hcmut.irms.menu_service.service;

import com.hcmut.irms.menu_service.model.MenuItem;
import com.hcmut.irms.menu_service.port.MenuConfirmationPublisherPort;
import com.hcmut.irms.menu_service.port.MenuItemBulkReader;
import com.hcmut.irms.menu_service.usecase.ConfirmOrderMenuAvailabilityUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderMenuAvailabilityService implements ConfirmOrderMenuAvailabilityUseCase {
    private final MenuItemBulkReader menuItemBulkReader;
    private final MenuConfirmationPublisherPort menuConfirmationPublisher;

    public OrderMenuAvailabilityService(MenuItemBulkReader menuItemBulkReader,
                                        MenuConfirmationPublisherPort menuConfirmationPublisher) {
        this.menuItemBulkReader = menuItemBulkReader;
        this.menuConfirmationPublisher = menuConfirmationPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public void confirmAvailability(OrderMenuAvailabilityCommand command) {
        if (command == null || command.orderId() == null || command.orderId().isBlank()) {
            throw new IllegalArgumentException("orderId is required in OrderCreatedEvent");
        }

        boolean isAvailable = areAllItemsAvailable(command.menuItemIds());
        menuConfirmationPublisher.publish(command.orderId(), isAvailable);
    }

    private boolean areAllItemsAvailable(List<String> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return false;
        }

        List<UUID> menuItemIds = itemIds.stream()
                .map(this::toUuid)
                .toList();

        if (menuItemIds.stream().anyMatch(id -> id == null)) {
            return false;
        }

        Map<UUID, Boolean> availabilityById = new HashMap<>();
        for (MenuItem item : menuItemBulkReader.findAllById(menuItemIds)) {
            availabilityById.put(item.getId(), item.isAvailable());
        }

        for (UUID menuItemId : menuItemIds) {
            if (!Boolean.TRUE.equals(availabilityById.get(menuItemId))) {
                return false;
            }
        }
        return true;
    }

    private UUID toUuid(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }

        try {
            return UUID.fromString(rawValue);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
