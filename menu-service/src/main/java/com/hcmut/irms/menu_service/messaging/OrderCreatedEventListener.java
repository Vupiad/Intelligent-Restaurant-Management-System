package com.hcmut.irms.menu_service.messaging;

import com.hcmut.irms.menu_service.messaging.event.OrderCreatedEvent;
import com.hcmut.irms.menu_service.model.MenuItem;
import com.hcmut.irms.menu_service.repository.MenuItemRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class OrderCreatedEventListener {
    private final MenuItemRepository menuItemRepository;
    private final MenuConfirmPublisher menuConfirmPublisher;

    public OrderCreatedEventListener(MenuItemRepository menuItemRepository, MenuConfirmPublisher menuConfirmPublisher) {
        this.menuItemRepository = menuItemRepository;
        this.menuConfirmPublisher = menuConfirmPublisher;
    }

    @RabbitListener(queues = "${app.rabbitmq.order-created-queue:kds.order.created}")
    @Transactional(readOnly = true)
    public void handle(OrderCreatedEvent event) {
        if (event == null || event.orderId() == null || event.orderId().isBlank()) {
            throw new IllegalArgumentException("orderId is required in OrderCreatedEvent");
        }

        boolean isAvailable = areAllItemsAvailable(event);
        menuConfirmPublisher.publish(event.orderId(), isAvailable);
    }

    private boolean areAllItemsAvailable(OrderCreatedEvent event) {
        if (event == null || event.items() == null || event.items().isEmpty()) {
            return false;
        }

        List<UUID> menuItemIds = event.items().stream()
                .map(OrderCreatedEvent.OrderItemPayload::menuItemId)
                .map(this::toUuid)
                .toList();

        if (menuItemIds.stream().anyMatch(id -> id == null)) {
            return false;
        }

        Map<UUID, Boolean> availabilityById = new HashMap<>();
        for (MenuItem item : menuItemRepository.findAllById(menuItemIds)) {
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
