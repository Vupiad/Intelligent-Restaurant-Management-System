package com.hcmut.irms.kds_service.infrastructure.api;

import com.hcmut.irms.kds_service.application.port.in.TicketReadUseCase;
import com.hcmut.irms.kds_service.application.port.in.TicketWriteUseCase;
import com.hcmut.irms.kds_service.domain.model.KitchenTicket;
import com.hcmut.irms.kds_service.infrastructure.api.dto.UpdateItemStatusRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kds/tickets")
public class KdsController {
    private final TicketWriteUseCase writeUseCase;
    private final TicketReadUseCase readUseCase;

    public KdsController(TicketWriteUseCase writeUseCase, TicketReadUseCase readUseCase) {
        this.writeUseCase = writeUseCase;
        this.readUseCase = readUseCase;
    }

    @GetMapping("/active")
    public ResponseEntity<List<KitchenTicket>> getActiveTickets() {
        return ResponseEntity.ok(readUseCase.getActiveTickets());
    }

    @PutMapping("/{ticketId}/items/{itemIndex}/status")
    public ResponseEntity<KitchenTicket> updateItemStatus(@PathVariable String ticketId,
                                                          @PathVariable int itemIndex,
                                                          @RequestBody UpdateItemStatusRequest request) {
        if (request == null || request.status() == null) {
            throw new IllegalArgumentException("Item status must be provided");
        }
        return ResponseEntity.ok(writeUseCase.updateItemStatus(ticketId, itemIndex, request.status()));
    }

    @PutMapping("/{ticketId}/ready")
    public ResponseEntity<Void> markTicketReady(@PathVariable String ticketId) {
        writeUseCase.markTicketReady(ticketId);
        return ResponseEntity.noContent().build();
    }
}
