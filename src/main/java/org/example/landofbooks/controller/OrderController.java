package org.example.landofbooks.controller;

import org.example.landofbooks.dto.OrderDetailsDTO;
import org.example.landofbooks.dto.OrderRequestDTO;
import org.example.landofbooks.dto.OrdersDTO;
import org.example.landofbooks.entity.User;
import org.example.landofbooks.repo.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/place")
    public ResponseEntity<Object> placeOrder(@RequestBody OrderRequestDTO orderRequest) {
        if (orderRequest.getOrderDetailsList() == null || orderRequest.getOrderDetailsList().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Order details cannot be null or empty"));
        }

        // Retrieve the userId from the orderRequest
        UUID userId = orderRequest.getUserId();

        // Fetch the User entity from the database (User should have the actual address and contact)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Use custom address if provided in the orderRequestDTO, otherwise use the user's default address
        String addressToUse = (orderRequest.getAddress() != null && !orderRequest.getAddress().isEmpty())
                ? orderRequest.getAddress()
                : user.getAddress();

        // Use custom contact if provided in the orderRequestDTO, otherwise use the user's default contact
        String contactToUse = (orderRequest.getContact() != null && !orderRequest.getContact().isEmpty())
                ? orderRequest.getContact()
                : user.getContact();
// Add logging for address and contact to check if the right values are passed
        System.out.println("Address to Use: " + addressToUse);
        System.out.println("Contact to Use: " + contactToUse);

        // Now call the service method with the correct address and contact values
        orderService.placeOrder(userId, orderRequest.getTotalPrice(), orderRequest.getOrderDetailsList(), addressToUse, contactToUse);

        return ResponseEntity.ok(Map.of("message", "Order placed successfully"));
    }

    @GetMapping("/daily-count")
    public ResponseEntity<List<Map<String, Object>>> getDailyStats() {
        return ResponseEntity.ok(orderService.getDailyOrdersWithRevenue());
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<OrdersDTO>> getAllOrders() {
        List<OrdersDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    @PutMapping("/updateStatus/{orderId}")
    public ResponseEntity<Object> updateOrderStatus(
            @PathVariable("orderId") UUID orderId,
            @RequestBody Map<String, String> request) {

        String status = request.get("status");
        boolean isUpdated = orderService.updateOrderStatus(orderId, status);

        if (isUpdated) {
            return ResponseEntity.ok("Order status updated successfully");
        } else {
            return ResponseEntity.status(404).body("Order not found");
        }
    }
}