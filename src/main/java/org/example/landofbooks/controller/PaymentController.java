package org.example.landofbooks.controller;

import org.example.landofbooks.dto.OrderRequestDTO;
import org.example.landofbooks.dto.PaymentResponseDTO;
import org.example.landofbooks.entity.Orders;
import org.example.landofbooks.entity.User;
import org.example.landofbooks.repo.UserRepository;
import org.example.landofbooks.service.OrderService;
import org.example.landofbooks.service.impl.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/payment-success")
    public ResponseEntity<?> handlePaymentSuccess(@RequestParam Map<String, String> params) {
        // Required PayHere parameters
        String orderId = params.get("order_id");
        String paymentStatus = params.get("status_code"); // status_code == 2 means success

        if (orderId == null || paymentStatus == null) {
            return ResponseEntity.badRequest().body("Missing payment details");
        }

        // Only save the order if payment was successful
        if (paymentStatus.equals("2")) {
            boolean updated = orderService.updateOrderStatusToSuccess(orderId);
            if (updated) {
                return ResponseEntity.ok("Order confirmed");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
            }
        }

        return ResponseEntity.ok("Payment not successful, order not updated");
    }
}