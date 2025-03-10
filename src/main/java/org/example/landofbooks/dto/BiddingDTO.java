package org.example.landofbooks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BiddingDTO {

    private UUID bidId; // Unique bid identifier
    private UUID bookId; // The book being bid on
    private UUID userId; // The user placing the bid
    private UUID categoryId; // The category of the book
    private double bidAmount; // The amount of the bid
    private String author;
    private LocalDateTime bidDate; // The date and time of the bid
    private String status; // Status of the bid (active, closed, won)
    private String image;

}
