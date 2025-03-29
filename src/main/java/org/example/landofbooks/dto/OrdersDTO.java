package org.example.landofbooks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrdersDTO {
    private UUID oid;
    private UUID userId;
    private double totalPrice;
    private String address;
    private String contact;
}
