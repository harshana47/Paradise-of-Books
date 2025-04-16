package org.example.landofbooks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderDetailsDTO {
    private UUID odid;
    private UUID orderId;
    private UUID userId;
    private double price;
    private String title;
    private LocalDateTime orderDate;
}
