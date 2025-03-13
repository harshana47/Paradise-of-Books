package org.example.landofbooks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidCartDTO {
    private UUID bcid;
    private double maxPrice;
    private UUID biddingId;
    private UUID userId;
    private String title;

}
