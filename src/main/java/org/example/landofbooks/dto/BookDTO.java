package org.example.landofbooks.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookDTO {
    private UUID id;
    private String title;
    private String author;
    private String bookStatus; // "new" or "used"
    private double price;
    private int qty;
    private String description;
    private String image;
    private String publishedYear;
    private UUID categoryId;
    private UUID userId;
    private String activeStatus;
}
