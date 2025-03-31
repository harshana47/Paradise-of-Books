package org.example.landofbooks.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacUtils;
import org.example.landofbooks.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/payment-webhook")
public class PaymentWebhookController {
    @Value("${payhere.merchant.id}")
    private String merchantId;

    @Value("${payhere.secret}")
    private String secretKey;
    private final OrderService orderService;

    public PaymentWebhookController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity handlePaymentNotification(
            @RequestBody Map<String, String> payload,
            HttpServletRequest request) {

        // Verify hash to ensure the notification is from PayHere
        String receivedHash = payload.get("hash");
        String calculatedHash = generateVerificationHash(payload);

        if (!receivedHash.equals(calculatedHash)) {
            return ResponseEntity.status(403).build();  // Unauthorized
        }

        // Process the payment status
        String status = payload.get("status");
        String orderId = payload.get("order_id");

        if ("2".equals(status)) { // "2" is the success status code
            orderService.updateOrderStatus(UUID.fromString(orderId),"COMPLETED");  // Mark the order as paid in your database
        }

        return ResponseEntity.ok().build();
    }

    private String generateVerificationHash(Map<String, String> params) {
        // Create hash using PayHere's hash generation rules
        String data = String.join("|",
                params.get("merchant_id"),
                params.get("order_id"),
                params.get("amount"),
                params.get("currency"),
                DigestUtils.md5Hex(params.toString()).toUpperCase()
        );
        return HmacUtils.hmacSha256Hex(secretKey, data);  // Using the secret key from your PayHere account
    }
}
