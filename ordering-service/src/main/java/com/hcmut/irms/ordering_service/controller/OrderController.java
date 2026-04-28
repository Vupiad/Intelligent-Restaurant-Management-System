package com.hcmut.irms.ordering_service.controller;

import com.hcmut.irms.ordering_service.dto.api.CreateOrderRequest;
import com.hcmut.irms.ordering_service.dto.api.OrderResponse;
import com.hcmut.irms.ordering_service.usecase.create.CreateOrderUseCase;
import com.hcmut.irms.ordering_service.usecase.get.GetOrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * REST entry point for order operations.
 * Contains NO business logic — delegates entirely to use cases.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;

    /**
     * Create a new order.
     * Requires role: MANAGER or SERVER
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request,
            Principal principal) {

        // Extract raw JWT token value to forward to menu-service
        String bearerToken = extractToken(principal);
        OrderResponse response = createOrderUseCase.createOrder(request, bearerToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get a single order by ID.
     * Requires role: MANAGER or SERVER
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(getOrderUseCase.getOrder(orderId));
    }

    /**
     * Get all orders.
     * Requires role: MANAGER or SERVER
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(getOrderUseCase.getAllOrders());
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private String extractToken(Principal principal) {
        if (principal instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }
        return "";
    }
}
