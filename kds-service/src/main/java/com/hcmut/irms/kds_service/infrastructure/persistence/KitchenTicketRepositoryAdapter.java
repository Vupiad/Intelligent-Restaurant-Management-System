package com.hcmut.irms.kds_service.infrastructure.persistence;

import com.hcmut.irms.kds_service.application.port.out.ActiveKitchenTicketReader;
import com.hcmut.irms.kds_service.application.port.out.KitchenTicketFinder;
import com.hcmut.irms.kds_service.application.port.out.KitchenTicketSaver;
import com.hcmut.irms.kds_service.domain.model.KitchenTicket;
import com.hcmut.irms.kds_service.domain.model.TicketStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class KitchenTicketRepositoryAdapter implements KitchenTicketSaver, KitchenTicketFinder, ActiveKitchenTicketReader {
    private final KitchenTicketMongoRepository repository;

    public KitchenTicketRepositoryAdapter(KitchenTicketMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public KitchenTicket save(KitchenTicket ticket) {
        return toDomain(repository.save(toDocument(ticket)));
    }

    @Override
    public Optional<KitchenTicket> findById(String ticketId) {
        return repository.findById(ticketId).map(this::toDomain);
    }

    @Override
    public List<KitchenTicket> findByStatusNotIn(List<TicketStatus> statuses) {
        return repository.findByStatusNotIn(statuses).stream()
                .map(this::toDomain)
                .toList();
    }

    private KitchenTicketDocument toDocument(KitchenTicket ticket) {
        KitchenTicketDocument document = new KitchenTicketDocument();
        document.setId(ticket.getId());
        document.setTableNumber(ticket.getTableNumber());
        document.setWaiterId(ticket.getWaiterId());
        document.setStatus(ticket.getStatus());
        document.setReceivedAt(ticket.getReceivedAt());
        document.setCompletedAt(ticket.getCompletedAt());
        document.setItems(ticket.getItems());
        return document;
    }

    private KitchenTicket toDomain(KitchenTicketDocument document) {
        return new KitchenTicket(
                document.getId(),
                document.getTableNumber(),
                document.getWaiterId(),
                document.getStatus(),
                document.getReceivedAt(),
                document.getCompletedAt(),
                document.getItems()
        );
    }
}
