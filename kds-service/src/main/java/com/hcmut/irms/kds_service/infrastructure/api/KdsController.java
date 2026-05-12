package com.hcmut.irms.kds_service.infrastructure.api;

import com.hcmut.irms.kds_service.application.port.in.GetActiveTicketsUseCase;
import com.hcmut.irms.kds_service.application.port.in.ListTicketStatusesUseCase;
import com.hcmut.irms.kds_service.application.port.in.UpdateTicketStatusUseCase;
import com.hcmut.irms.kds_service.domain.model.KitchenTicket;
import com.hcmut.irms.kds_service.domain.model.TicketStatus;
import com.hcmut.irms.kds_service.infrastructure.api.dto.UpdateOrderStatusRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kds/tickets")
public class KdsController {
    private final UpdateTicketStatusUseCase updateTicketStatusUseCase;
    private final GetActiveTicketsUseCase getActiveTicketsUseCase;
    private final ListTicketStatusesUseCase listTicketStatusesUseCase;

    public KdsController(UpdateTicketStatusUseCase updateTicketStatusUseCase,
                         GetActiveTicketsUseCase getActiveTicketsUseCase,
                         ListTicketStatusesUseCase listTicketStatusesUseCase) {
        this.updateTicketStatusUseCase = updateTicketStatusUseCase;
        this.getActiveTicketsUseCase = getActiveTicketsUseCase;
        this.listTicketStatusesUseCase = listTicketStatusesUseCase;
    }

    @GetMapping("/active")
    public ResponseEntity<List<KitchenTicket>> getActiveTickets() {
        return ResponseEntity.ok(getActiveTicketsUseCase.getActiveTickets());
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<TicketStatus>> getTicketStatuses() {
        return ResponseEntity.ok(listTicketStatusesUseCase.getTicketStatuses());
    }

    @PutMapping("/{ticketId}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable String ticketId,
            @RequestBody UpdateOrderStatusRequest request) {
        updateTicketStatusUseCase.updateOrderStatus(ticketId, request.status());
        return ResponseEntity.noContent().build();
    }
}
