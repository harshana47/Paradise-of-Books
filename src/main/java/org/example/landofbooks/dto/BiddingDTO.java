package org.example.landofbooks.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BiddingDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private UUID bidId; // Unique bid identifier
    private UUID userId; // The user placing the bid
    private UUID categoryId; // The category of the book
    private double bidAmount; // The amount of the bid
    private String description;
    private String author;
    private String title;
    private LocalDateTime bidDate; // The date and time of the bid
    private String status; // Status of the bid (active, closed, won)
    private String image;

    public BiddingDTO(UUID bidId, String title, String author,String description, String image, LocalDateTime bidDate, double bidAmount) {
        this.bidId = bidId;
        this.title = title;
        this.author = author;
        this.image = image;
        this.bidDate = bidDate;
        this.description = description;
        this.bidAmount = bidAmount;
    }

    public BiddingDTO(UUID bidId, String title, String author, String description, String image, LocalDate bidDate, double bidAmount, String status) {
        this.bidId = bidId;
        this.title = title;
        this.author = author;
        this.image = image;
        this.description = description;
        this.bidDate = bidDate.atStartOfDay();
        this.bidAmount = bidAmount;
        this.status = status;
    }
}
