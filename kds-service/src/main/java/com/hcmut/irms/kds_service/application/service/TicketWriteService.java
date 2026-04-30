package com.hcmut.irms.kds_service.application.service;

import com.hcmut.irms.kds_service.application.exception.TicketNotFoundException;
import com.hcmut.irms.kds_service.application.port.in.TicketWriteUseCase;
import com.hcmut.irms.kds_service.application.port.out.KdsWebSocketPublisher;
import com.hcmut.irms.kds_service.application.port.out.OrderStatusPublisher;
import com.hcmut.irms.kds_service.domain.model.ItemStatus;
import com.hcmut.irms.kds_service.domain.model.KitchenTicket;
import com.hcmut.irms.kds_service.domain.model.TicketItem;
import com.hcmut.irms.kds_service.domain.model.TicketStatus;
import com.hcmut.irms.kds_service.domain.repository.KitchenTicketRepository;
import com.hcmut.irms.kds_service.infrastructure.messaging.event.OrderCreatedEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.List;

@Service
public class TicketWriteService implements TicketWriteUseCase {
    private final KitchenTicketRepository repository;
    private final KdsWebSocketPublisher webSocketPublisher;
    private final OrderStatusPublisher orderStatusPublisher;

    public TicketWriteService(KitchenTicketRepository repository, KdsWebSocketPublisher webSocketPublisher,
                              OrderStatusPublisher orderStatusPublisher) {
        this.repository = repository;
        this.webSocketPublisher = webSocketPublisher;
        this.orderStatusPublisher = orderStatusPublisher;
    }

    @Override
    public void createTicketFromEvent(OrderCreatedEvent event) {
        KitchenTicket ticket = new KitchenTicket();
        ticket.setId(event.orderId());
        ticket.setTableNumber(event.tableNumber());
        ticket.setWaiterId(event.waiterId());
        ticket.setStatus(TicketStatus.PENDING);
        ticket.setReceivedAt(parseTimestamp(event.timestamp()));
        ticket.setCompletedAt(null);
        ticket.setItems(toTicketItems(event.items()));
        KitchenTicket saved = repository.save(ticket);
        webSocketPublisher.broadcastNewTicket(saved);
    }


    @Override
    public KitchenTicket updateItemStatus(String ticketId, int itemIndex, ItemStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Item status is required");
        }

        KitchenTicket ticket = repository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException(ticketId));
        List<TicketItem> items = ticket.getItems();
        if (itemIndex < 0 || itemIndex >= items.size()) {
            throw new IllegalArgumentException("Invalid item index: " + itemIndex);
        }

        items.get(itemIndex).setStatus(status);

        TicketStatus previousStatus = ticket.getStatus();
        boolean ticketCompleted = recalculateTicketStatus(ticket);
        KitchenTicket saved = repository.save(ticket);

        if (saved.getStatus() != previousStatus) {
            orderStatusPublisher.publishOrderStatusEvent(saved.getId(), saved.getStatus());
        }

        if (ticketCompleted) {
            webSocketPublisher.broadcastTicketRemoval(saved.getId());
            return saved;
        }

        webSocketPublisher.broadcastTicketUpdate(saved);
        return saved;
    }

    @Override
    public void updateOrderStatus(String ticketId, TicketStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }
        if (!EnumSet.of(TicketStatus.COOKING, TicketStatus.READY, TicketStatus.SERVED).contains(status)) {
            throw new IllegalArgumentException("Status must be COOKING, READY, or SERVED");
        }

        KitchenTicket ticket = repository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException(ticketId));
        ticket.setStatus(status);
        ticket.setCompletedAt(status == TicketStatus.COOKING ? null : LocalDateTime.now(ZoneOffset.UTC));

        KitchenTicket saved = repository.save(ticket);
        orderStatusPublisher.publishOrderStatusEvent(saved.getId(), status);
        if (status == TicketStatus.COOKING) {
            webSocketPublisher.broadcastTicketUpdate(saved);
            return;
        }
        webSocketPublisher.broadcastTicketRemoval(saved.getId());
    }

    private List<TicketItem> toTicketItems(List<OrderCreatedEvent.OrderItemPayload> items) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(i -> new TicketItem(i.menuItemId(), i.itemName(), i.quantity(), ItemStatus.PENDING, i.customizations()))
                .toList();
    }

    private LocalDateTime parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isBlank()) {
            return LocalDateTime.now(ZoneOffset.UTC);
        }
        return OffsetDateTime.parse(timestamp).withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    private boolean recalculateTicketStatus(KitchenTicket ticket) {
        if (ticket.getItems().isEmpty()) {
            ticket.setStatus(TicketStatus.PENDING);
            ticket.setCompletedAt(null);
            return false;
        }

        boolean allReady = ticket.getItems().stream().allMatch(i -> i.getStatus() == ItemStatus.READY);
        if (allReady) {
            ticket.setStatus(TicketStatus.READY);
            ticket.setCompletedAt(LocalDateTime.now(ZoneOffset.UTC));
            return true;
        }

        boolean anyReady = ticket.getItems().stream().anyMatch(i -> i.getStatus() == ItemStatus.READY);
        ticket.setStatus(anyReady ? TicketStatus.COOKING : TicketStatus.PENDING);
        ticket.setCompletedAt(null);
        return false;
    }
}
