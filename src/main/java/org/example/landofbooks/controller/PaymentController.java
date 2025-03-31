package org.example.landofbooks.controller;

import org.example.landofbooks.dto.OrderRequestDTO;
import org.example.landofbooks.dto.PaymentResponseDTO;
import org.example.landofbooks.entity.Orders;
import org.example.landofbooks.entity.User;
import org.example.landofbooks.repo.UserRepository;
import org.example.landofbooks.service.OrderService;
import org.example.landofbooks.service.impl.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final OrderService orderService;

    public PaymentController(PaymentService paymentService, UserRepository userRepository, OrderService orderService) {
        this.paymentService = paymentService;
        this.userRepository = userRepository;
        this.orderService = orderService;
    }

    @PostMapping("/create-payment")
    public ResponseEntity<Map<String, String>> createPaymentRequest(@RequestBody OrderRequestDTO orderRequest) {
        try {
            System.out.println("Received Payment Request for Total Price: " + orderRequest.getTotalPrice());

            // Fetch the User entity from the database using userId
            User user = userRepository.findById(orderRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Create Orders entity and set values
            Orders order = new Orders();
            order.setUser(user);  // Assign the User object instead of userId
            order.setTotalPrice(orderRequest.getTotalPrice());
            order.setAddress(orderRequest.getAddress() != null && !orderRequest.getAddress().isEmpty()
                    ? orderRequest.getAddress() : user.getAddress());
            order.setContact(orderRequest.getContact() != null && !orderRequest.getContact().isEmpty()
                    ? orderRequest.getContact() : user.getContact());
            order.setStatus("INCOMPLETE"); // Default status

            // Call payment service with the mapped Orders entity
            Map<String, String> paymentData = paymentService.createPaymentRequest(order);
            return ResponseEntity.ok(paymentData);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating payment request: " + e.getMessage());
        }
    }

    @PostMapping("/payment-success")
    public ResponseEntity<String> handlePaymentSuccess(@RequestBody PaymentResponseDTO paymentResponse) {
        try {
            System.out.println("Payment success received: " + paymentResponse);

            // Validate payment response (check status, hash, etc.)
            if (!paymentResponse.getStatus().equals("SUCCESS")) {
                throw new RuntimeException("Invalid payment status");
            }

            // Retrieve the user ID from the payment response
            UUID userId = paymentResponse.getUserId();

            // Fetch user details from DB
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Use provided address/contact or fall back to user's defaults
            String addressToUse = (paymentResponse.getAddress() != null && !paymentResponse.getAddress().isEmpty())
                    ? paymentResponse.getAddress()
                    : user.getAddress();

            String contactToUse = (paymentResponse.getContact() != null && !paymentResponse.getContact().isEmpty())
                    ? paymentResponse.getContact()
                    : user.getContact();

            // Debug logs to check address & contact
            System.out.println("Address to Use: " + addressToUse);
            System.out.println("Contact to Use: " + contactToUse);

            // Place order after successful payment (Same as COD logic)
            orderService.placeOrder(
                    userId,
                    paymentResponse.getAmount(),
                    paymentResponse.getOrderDetails(),
                    addressToUse,
                    contactToUse
            );

            return ResponseEntity.ok("Order placed successfully after payment!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing payment success: " + e.getMessage());
        }
    }

}
