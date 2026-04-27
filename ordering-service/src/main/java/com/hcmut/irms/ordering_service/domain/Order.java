package com.hcmut.irms.ordering_service.domain;

import com.hcmut.irms.ordering_service.domain.exception.InvalidStatusTransitionException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_number", nullable = false)
    private String tableNumber;

    @Column(name = "staff_name", nullable = false)
    private String staffName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    // ─── Factory ────────────────────────────────────────────────────────────

    public static Order create(String tableNumber, String staffName, List<OrderItem> items) {
        Order order = new Order();
        order.tableNumber = tableNumber;
        order.staffName   = staffName;
        order.status      = OrderStatus.CREATED;
        order.timestamp   = LocalDateTime.now();
        items.forEach(i -> i.setOrder(order));
        order.items       = new ArrayList<>(items);
        return order;
    }

    // ─── Business Rule ───────────────────────────────────────────────────────

    /**
     * Applies a validated status transition.
     * Throws {@link InvalidStatusTransitionException} for illegal transitions.
     */
    public void applyStatusTransition(OrderStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(this.status, newStatus);
        }
        this.status = newStatus;
    }
}
