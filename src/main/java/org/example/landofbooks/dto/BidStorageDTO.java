package org.example.landofbooks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BidStorageDTO {
    private UUID bSId;
    private double maxPrice;
    private UUID biddingId;
    private UUID userId;

}
