package com.hcmut.irms.kds_service.application.mapper;

import com.hcmut.irms.kds_service.application.command.CreateTicketCommand;
import com.hcmut.irms.kds_service.domain.model.KitchenTicket;
import com.hcmut.irms.kds_service.domain.model.TicketItem;
import com.hcmut.irms.kds_service.domain.model.TicketStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class KitchenTicketMapper {
    public KitchenTicket from(CreateTicketCommand command) {
        KitchenTicket ticket = new KitchenTicket();
        ticket.setId(command.orderId());
        ticket.setTableNumber(command.tableNumber());
        ticket.setWaiterId(command.waiterId());
        ticket.setStatus(TicketStatus.WAIT_FOR_MENU_CONFIRM);
        ticket.setReceivedAt(parseTimestamp(command.timestamp()));
        ticket.setCompletedAt(null);
        ticket.setItems(toTicketItems(command.items()));
        return ticket;
    }

    private List<TicketItem> toTicketItems(List<CreateTicketCommand.Item> items) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(i -> new TicketItem(i.menuItemId(), i.itemName(), i.quantity(), i.customizations(), i.notes()))
                .toList();
    }

    private LocalDateTime parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isBlank()) {
            return LocalDateTime.now(ZoneOffset.UTC);
        }
        return OffsetDateTime.parse(timestamp).withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }
}
