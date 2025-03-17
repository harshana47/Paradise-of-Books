package org.example.landofbooks.service.impl;

import jakarta.transaction.Transactional;
import org.example.landofbooks.dto.OrderDetailsDTO;
import org.example.landofbooks.entity.OrderDetails;
import org.example.landofbooks.entity.Orders;
import org.example.landofbooks.entity.User;
import org.example.landofbooks.repo.BidCartRepo;
import org.example.landofbooks.repo.OrderDetailsRepository;
import org.example.landofbooks.repo.OrderRepository;
import org.example.landofbooks.repo.UserRepository;
import org.example.landofbooks.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BidCartRepo bidCartRepository;

    @Override
    @Transactional
    public void placeOrder(UUID userId, double totalPrice, List<OrderDetailsDTO> orderDetailsList) {
        // Fetch the user entity
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create and save the order
        Orders savedOrder = orderRepository.save(new Orders(null, totalPrice, user));

        // Convert DTOs to OrderDetails entities
        List<OrderDetails> orderDetailsEntities = orderDetailsList.stream()
                .map(dto -> new OrderDetails(
                        UUID.randomUUID(),
                        dto.getPrice(),  // price comes second
                        user,
                        savedOrder, // order should come after user
                        dto.getTitle(),
                        LocalDateTime.now()
                ))
                .collect(Collectors.toList());

        orderDetailsRepository.saveAll(orderDetailsEntities);
        bidCartRepository.deleteByUser_uid(userId);
    }
}

