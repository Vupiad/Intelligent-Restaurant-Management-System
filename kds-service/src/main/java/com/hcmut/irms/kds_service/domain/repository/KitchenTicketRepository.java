package com.hcmut.irms.kds_service.domain.repository;

import com.hcmut.irms.kds_service.domain.model.KitchenTicket;
import com.hcmut.irms.kds_service.domain.model.TicketStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface KitchenTicketRepository extends MongoRepository<KitchenTicket, String> {
    List<KitchenTicket> findByStatusNot(TicketStatus status);
}
