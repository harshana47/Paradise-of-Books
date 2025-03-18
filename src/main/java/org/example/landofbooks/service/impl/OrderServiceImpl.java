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

import org.modelmapper.ModelMapper;

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

    @Autowired
    private ModelMapper modelMapper;  // Inject ModelMapper

    @Override
    @Transactional
    public void placeOrder(UUID userId, double totalPrice, List<OrderDetailsDTO> orderDetailsList) {
        if (orderDetailsList == null || orderDetailsList.isEmpty()) {
            throw new RuntimeException("Order details cannot be null or empty");
        }

        // Fetch the user entity
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create and save the order
        Orders savedOrder = orderRepository.save(new Orders(null, totalPrice, user));

        // Convert DTOs to OrderDetails entities using ModelMapper
        List<OrderDetails> orderDetailsEntities = orderDetailsList.stream()
                .map(dto -> modelMapper.map(dto, OrderDetails.class))  // Use ModelMapper to map DTO to entity
                .collect(Collectors.toList());

        // Set additional properties on the OrderDetails entities
        orderDetailsEntities.forEach(orderDetails -> {
            orderDetails.setOrders(savedOrder);  // Set the order relation
            orderDetails.setUser(user);         // Set the user relation
            orderDetails.setOrderDate(LocalDateTime.now());
        });

        // Save the order details
        orderDetailsRepository.saveAll(orderDetailsEntities);

        // Clean up the cart after placing the order
        bidCartRepository.deleteByUser_uid(userId);
    }
}


