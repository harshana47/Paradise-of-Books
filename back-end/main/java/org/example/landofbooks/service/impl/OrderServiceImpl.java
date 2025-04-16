package org.example.landofbooks.service.impl;

import jakarta.transaction.Transactional;
import org.example.landofbooks.dto.OrderDetailsDTO;
import org.example.landofbooks.dto.OrdersDTO;
import org.example.landofbooks.dto.UserDTO;
import org.example.landofbooks.entity.OrderDetails;
import org.example.landofbooks.entity.Orders;
import org.example.landofbooks.entity.User;
import org.example.landofbooks.repo.BidCartRepo;
import org.example.landofbooks.repo.OrderDetailsRepository;
import org.example.landofbooks.repo.OrderRepository;
import org.example.landofbooks.repo.UserRepository;
import org.example.landofbooks.service.OrderService;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public void placeOrder(UUID userId, double totalPrice, List<OrderDetailsDTO> orderDetailsList, String address, String contact) {
        if (orderDetailsList == null || orderDetailsList.isEmpty()) {
            throw new RuntimeException("Order details cannot be null or empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Orders savedOrder = orderRepository.save(new Orders(null, totalPrice, user, address, contact,"INCOMPLETE"));

        List<OrderDetails> orderDetailsEntities = orderDetailsList.stream()
                .map(dto -> modelMapper.map(dto, OrderDetails.class))
                .collect(Collectors.toList());

        orderDetailsEntities.forEach(orderDetails -> {
            orderDetails.setOrders(savedOrder);
            orderDetails.setUser(user);
            orderDetails.setOrderDate(LocalDateTime.now());
        });

        orderDetailsRepository.saveAll(orderDetailsEntities);

        bidCartRepository.deleteByUser_uid(userId);
    }


    @Override
    public List<Map<String, Object>> getDailyOrdersWithRevenue() {
        List<Object[]> results = orderDetailsRepository.getDailyOrdersWithRevenue();

        return results.stream()
                .map(obj -> {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("orderDate", ((java.sql.Date) obj[0]).toLocalDate());
                    resultMap.put("orderCount", ((Number) obj[1]).longValue());
                    resultMap.put("revenue", ((Number) obj[2]).doubleValue());
                    return resultMap;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<OrdersDTO> getAllOrders() {
        return modelMapper.map(orderRepository.findAll(), new TypeToken<List<OrdersDTO>>() {}.getType());
    }

    @Override
    public boolean updateOrderStatus(UUID orderId, String status) {
        Optional<Orders> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isPresent()) {
            Orders order = optionalOrder.get();
            order.setStatus(status);
            orderRepository.save(order);
            return true;
        }

        return false;
    }

    @Override
    public boolean updateOrderStatusToSuccess(UUID orderId) {

            return true;


    }

}

