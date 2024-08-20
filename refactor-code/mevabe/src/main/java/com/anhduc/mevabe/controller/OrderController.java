package com.anhduc.mevabe.controller;

import com.anhduc.mevabe.dto.request.ConfirmOrderRequest;
import com.anhduc.mevabe.dto.request.CreateOrderRequest;
import com.anhduc.mevabe.dto.request.PreOrderRequest;
import com.anhduc.mevabe.entity.Order;
import com.anhduc.mevabe.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        Order createdOrder = orderService.createOrder(createOrderRequest);
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID orderId) {
        Optional<Order> order = orderService.getOrderById(orderId);
        return order.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/pre-order")
    public ResponseEntity<Order> createPreOrder(@RequestBody PreOrderRequest preOrderRequest) {
        try {
            Order preOrder = orderService.createPreOrder(preOrderRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(preOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/confirm/{orderId}")
    public ResponseEntity<Order> confirmOrder(@PathVariable UUID orderId, @RequestBody ConfirmOrderRequest request) {
        try {
            UUID shipperId = request.getShipperId();
            Order confirmedOrder = orderService.confirmOrder(orderId, shipperId);
            return ResponseEntity.ok(confirmedOrder);
        } catch (RuntimeException e) {
            System.err.println("Error in confirmOrder: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<Order> cancelOrder(@PathVariable UUID orderId) {
        try {
            Order canceledOrder = orderService.cancelOrder(orderId);
            return ResponseEntity.ok(canceledOrder);
        } catch (RuntimeException e) {
            System.err.println("Error in cancelOrder: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/cancel-shipping/{orderId}")
    public ResponseEntity<Order> cancelShippingOrder(@PathVariable UUID orderId) {
        try {
            Order canceledOrder = orderService.cancelShippingOrder(orderId);
            return ResponseEntity.ok(canceledOrder);
        } catch (RuntimeException e) {
            System.err.println("Error in cancelOrder: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/shipping/{orderId}")
    public ResponseEntity<Order> shippingOrder(@PathVariable UUID orderId) {
        try {
            Order canceledOrder = orderService.shippingOrder(orderId);
            return ResponseEntity.ok(canceledOrder);
        } catch (RuntimeException e) {
            System.err.println("Error in cancelOrder: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
