package org.example.landofbooks.controller;

import org.example.landofbooks.dto.OrderDetailsDTO;
import org.example.landofbooks.dto.OrderRequestDTO;
import org.example.landofbooks.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin("*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<Object> placeOrder(@RequestBody OrderRequestDTO orderRequest) {
        if (orderRequest.getOrderDetailsList() == null || orderRequest.getOrderDetailsList().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Order details cannot be null or empty"));
        }
        orderService.placeOrder(orderRequest.getUserId(), orderRequest.getTotalPrice(), orderRequest.getOrderDetailsList());

        return ResponseEntity.ok(Map.of("message", "Order placed successfully"));
    }

}

