package com.hcmut.irms.kds_service.application.service;

import com.hcmut.irms.kds_service.application.command.CreateTicketCommand;
import com.hcmut.irms.kds_service.application.mapper.KitchenTicketMapper;
import com.hcmut.irms.kds_service.application.port.out.KdsWebSocketPublisher;
import com.hcmut.irms.kds_service.application.port.out.KitchenTicketFinder;
import com.hcmut.irms.kds_service.application.port.out.KitchenTicketSaver;
import com.hcmut.irms.kds_service.application.port.out.OrderStatusPublisher;
import com.hcmut.irms.kds_service.domain.model.KitchenTicket;
import com.hcmut.irms.kds_service.domain.model.TicketItem;
import com.hcmut.irms.kds_service.domain.model.TicketStatus;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
class TicketWriteServiceTests {

    @Test
    void createTicketFromEventKeepsItemNotes() {
        AtomicReference<KitchenTicket> savedTicketRef = new AtomicReference<>();
        AtomicReference<KitchenTicket> broadcastTicketRef = new AtomicReference<>();

        KitchenTicketSaver ticketSaver = (KitchenTicketSaver) Proxy.newProxyInstance(
                KitchenTicketSaver.class.getClassLoader(),
                new Class[]{KitchenTicketSaver.class},
                (proxy, method, args) -> {
                    if ("save".equals(method.getName())) {
                        KitchenTicket ticket = (KitchenTicket) args[0];
                        savedTicketRef.set(ticket);
                        return ticket;
                    }
                    if ("toString".equals(method.getName())) {
                        return "StubKitchenTicketRepository";
                    }
                    throw new UnsupportedOperationException("Unexpected repository method: " + method.getName());
                }
        );

        KdsWebSocketPublisher webSocketPublisher = new KdsWebSocketPublisher() {
            @Override
            public void broadcastNewTicket(KitchenTicket ticket) {
                broadcastTicketRef.set(ticket);
            }

            @Override
            public void broadcastTicketUpdate(KitchenTicket ticket) {
                throw new UnsupportedOperationException("Not used in this test");
            }

            @Override
            public void broadcastTicketRemoval(String ticketId) {
                throw new UnsupportedOperationException("Not used in this test");
            }
        };

        OrderStatusPublisher orderStatusPublisher = (orderId, status) -> {
            throw new UnsupportedOperationException("Not used in this test");
        };

        TicketWriteService service = new TicketWriteService(
                ticketSaver,
                (KitchenTicketFinder) ticketId -> {
                    throw new UnsupportedOperationException("Not used in this test");
                },
                webSocketPublisher,
                orderStatusPublisher,
                new KitchenTicketMapper(),
                new TicketStatusPolicy()
        );
        CreateTicketCommand command = new CreateTicketCommand(
                "order-1",
                7,
                "waiter-1",
                "2026-04-30T09:15:00Z",
                List.of(new CreateTicketCommand.Item(
                        "mi-1",
                        "Fried Rice",
                        2,
                        List.of("Less spicy"),
                        List.of("No onion")
                ))
        );

        service.createTicket(command);

        KitchenTicket savedTicket = savedTicketRef.get();
        assertNotNull(savedTicket);
        assertEquals(1, savedTicket.getItems().size());
        assertEquals(TicketStatus.WAIT_FOR_MENU_CONFIRM, savedTicket.getStatus());

        TicketItem savedItem = savedTicket.getItems().get(0);
        assertEquals(List.of("Less spicy"), savedItem.getCustomizations());
        assertEquals(List.of("No onion"), savedItem.getNotes());
        assertEquals(savedTicket, broadcastTicketRef.get());
    }
}
