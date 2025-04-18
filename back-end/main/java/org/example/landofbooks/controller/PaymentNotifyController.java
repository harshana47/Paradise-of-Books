package org.example.landofbooks.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.landofbooks.entity.Orders;
import org.example.landofbooks.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin("*")
public class PaymentNotifyController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/notify")
    public ResponseEntity<?> handlePaymentSuccess(@RequestParam Map<UUID, String> params) {
//        String orderId = params.get("order_id");
        UUID orderId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        String paymentStatus = params.get("status_code");

        if (orderId == null || paymentStatus == null) {
            return ResponseEntity.badRequest().body("Missing payment details");
        }

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
    @GetMapping("/success")
    public ResponseEntity<String> handlePaymentSuccess(@RequestParam("order_id") String orderId) {
        System.out.println("Payment successful for Order ID: " + orderId);

        String redirectScript = """
        <html>
        <head>
            <script>
                window.location.href = "http://localhost:63342/Paradise-of-Bookss/front-end/user/bookCart.html";
            </script>
        </head>
        <body>
            Redirecting to bookCart...
        </body>
        </html>
    """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);

        return new ResponseEntity<>(redirectScript, headers, HttpStatus.OK);
    }
    @GetMapping("/successY")
    public ResponseEntity<String> handlePaymentSuccess1(@RequestParam("order_id") String orderId) {
        System.out.println("Payment successful for Order ID: " + orderId);

        String redirectScript = """
        <html>
        <head>
            <script>
                window.location.href = "http://localhost:63342/Paradise-of-Bookss/front-end/user/bidCart.html";
            </script>
        </head>
        <body>
            Redirecting to bidCart...
        </body>
        </html>
    """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);

        return new ResponseEntity<>(redirectScript, headers, HttpStatus.OK);
    }

}
