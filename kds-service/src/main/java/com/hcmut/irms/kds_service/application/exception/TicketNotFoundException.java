package com.hcmut.irms.kds_service.application.exception;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(String ticketId) {
        super("Kitchen ticket not found: " + ticketId);
    }
}
