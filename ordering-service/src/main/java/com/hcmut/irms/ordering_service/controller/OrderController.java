package com.hcmut.irms.ordering_service.controller;

import com.hcmut.irms.ordering_service.dto.api.CreateOrderRequest;
import com.hcmut.irms.ordering_service.dto.api.OrderResponse;
import com.hcmut.irms.ordering_service.mapper.OrderApiMapper;
import com.hcmut.irms.ordering_service.usecase.create.CreateOrderUseCase;
import com.hcmut.irms.ordering_service.usecase.get.GetOrderByIdUseCase;
import com.hcmut.irms.ordering_service.usecase.get.ListOrdersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final ListOrdersUseCase listOrdersUseCase;
    private final OrderApiMapper apiMapper;

    /**
     * Create a new order.
     * Requires role: MANAGER or SERVER
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request) {

        OrderResponse response = apiMapper.toResponse(createOrderUseCase.createOrder(apiMapper.toCommand(request)));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get a single order by ID.
     * Requires role: MANAGER or SERVER
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(apiMapper.toResponse(getOrderByIdUseCase.getOrder(orderId)));
    }

    /**
     * Get all orders.
     * Requires role: MANAGER or SERVER
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(listOrdersUseCase.getAllOrders().stream()
                .map(apiMapper::toResponse)
                .toList());
    }

}
