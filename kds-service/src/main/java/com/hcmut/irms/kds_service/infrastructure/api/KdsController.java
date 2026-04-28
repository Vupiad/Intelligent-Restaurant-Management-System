package com.hcmut.irms.kds_service.infrastructure.api;

import com.hcmut.irms.kds_service.application.port.in.TicketReadUseCase;
import com.hcmut.irms.kds_service.application.port.in.TicketWriteUseCase;
import com.hcmut.irms.kds_service.domain.model.KitchenTicket;
import com.hcmut.irms.kds_service.infrastructure.api.dto.UpdateOrderStatusRequest;
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


    @PutMapping("/{ticketId}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable String ticketId,
            @RequestBody UpdateOrderStatusRequest request) {
        writeUseCase.updateOrderStatus(ticketId, request.status());
        return ResponseEntity.noContent().build();
    }
}
