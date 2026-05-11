package com.hcmut.irms.kds_service.infrastructure.persistence;

import com.hcmut.irms.kds_service.domain.model.TicketItem;
import com.hcmut.irms.kds_service.domain.model.TicketStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "kitchen_tickets")
@TypeAlias("com.hcmut.irms.kds_service.domain.model.KitchenTicket")
public class KitchenTicketDocument {
    @Id
    private String id;
    private Integer tableNumber;
    private String waiterId;
    private TicketStatus status;
    private LocalDateTime receivedAt;
    private LocalDateTime completedAt;
    private List<TicketItem> items = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getWaiterId() {
        return waiterId;
    }

    public void setWaiterId(String waiterId) {
        this.waiterId = waiterId;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<TicketItem> getItems() {
        return items;
    }

    public void setItems(List<TicketItem> items) {
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
    }
}
