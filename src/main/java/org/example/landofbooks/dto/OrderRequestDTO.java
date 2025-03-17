package org.example.landofbooks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderRequestDTO {
    private UUID userId;
    private double totalPrice;
    private List<OrderDetailsDTO> orderDetailsList;
}

