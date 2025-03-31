package org.example.landofbooks.service;

import org.example.landofbooks.dto.OrderDetailsDTO;
import org.example.landofbooks.dto.OrdersDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrderService {
    public void placeOrder(UUID userId, double totalPrice, List<OrderDetailsDTO> orderDetailsList, String address, String contact);

    public List<Map<String, Object>> getDailyOrdersWithRevenue();

    List<OrdersDTO> getAllOrders();

    boolean updateOrderStatus(UUID orderId, String status);
}
