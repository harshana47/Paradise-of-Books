package org.example.landofbooks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookDTO {
    private UUID bid;
    private String title;
    private String author;
    private String bookStatus;
    private double price;
    private int qty;
    private String description;
    private String image;
    private String publishedYear;
    private UUID categoryId;
    private UUID userId;
    private String activeStatus;

    public BookDTO(UUID bid, String title, String author, double price, int qty, String publishedYear, String description, String bookStatus, String image) {

        this.bid = bid;
        this.title = title;
        this.author = author;
        this.price = price;
        this.qty = qty;
        this.publishedYear = publishedYear;
        this.description = description;
        this.bookStatus = bookStatus;
        this.image = image;

    }
}
