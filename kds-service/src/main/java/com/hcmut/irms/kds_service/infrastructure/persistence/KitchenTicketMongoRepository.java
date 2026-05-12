package com.hcmut.irms.kds_service.infrastructure.persistence;

import com.hcmut.irms.kds_service.domain.model.TicketStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface KitchenTicketMongoRepository extends MongoRepository<KitchenTicketDocument, String> {
    List<KitchenTicketDocument> findByStatusNotIn(List<TicketStatus> statuses);
}
