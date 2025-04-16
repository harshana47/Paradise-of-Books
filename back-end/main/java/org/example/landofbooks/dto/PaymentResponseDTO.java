package org.example.landofbooks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDTO {
    private String status;
    private UUID userId;
    private UUID OrderId;
    private Double amount;
    private List<OrderDetailsDTO> orderDetails;
    private String address;
    private String contact;
}