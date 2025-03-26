package org.example.landofbooks.service;

import org.example.landofbooks.dto.OrderDetailsDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OrderService {
    public void placeOrder(UUID userId, double totalPrice, List<OrderDetailsDTO> orderDetailsList);

    public List<Map<String, Object>> getDailyOrdersWithRevenue();
}
