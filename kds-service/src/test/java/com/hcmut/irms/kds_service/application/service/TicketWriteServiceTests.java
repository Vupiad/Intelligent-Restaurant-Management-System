package com.hcmut.irms.kds_service.application.service;

import com.hcmut.irms.kds_service.application.port.out.KdsWebSocketPublisher;
import com.hcmut.irms.kds_service.application.port.out.OrderStatusPublisher;
import com.hcmut.irms.kds_service.domain.model.KitchenTicket;
import com.hcmut.irms.kds_service.domain.model.TicketItem;
import com.hcmut.irms.kds_service.domain.repository.KitchenTicketRepository;
import com.hcmut.irms.kds_service.infrastructure.messaging.event.OrderCreatedEvent;
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

        KitchenTicketRepository repository = (KitchenTicketRepository) Proxy.newProxyInstance(
                KitchenTicketRepository.class.getClassLoader(),
                new Class[]{KitchenTicketRepository.class},
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

        TicketWriteService service = new TicketWriteService(repository, webSocketPublisher, orderStatusPublisher);
        OrderCreatedEvent event = new OrderCreatedEvent(
                "evt-1",
                "order-1",
                7,
                "waiter-1",
                "2026-04-30T09:15:00Z",
                List.of(new OrderCreatedEvent.OrderItemPayload(
                        "mi-1",
                        "Fried Rice",
                        2,
                        List.of("Less spicy"),
                        List.of("No onion")
                ))
        );

        service.createTicketFromEvent(event);

        KitchenTicket savedTicket = savedTicketRef.get();
        assertNotNull(savedTicket);
        assertEquals(1, savedTicket.getItems().size());

        TicketItem savedItem = savedTicket.getItems().get(0);
        assertEquals(List.of("Less spicy"), savedItem.getCustomizations());
        assertEquals(List.of("No onion"), savedItem.getNotes());
        assertEquals(savedTicket, broadcastTicketRef.get());
    }
}
