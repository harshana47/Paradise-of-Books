package org.example.landofbooks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.landofbooks.entity.Book;

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
    @NotBlank(message = "Published year is required")
    @Pattern(regexp = "^(19|20)\\d{2}$", message = "Year must be a valid 4-digit year between 1900 and 2099")
    private String publishedYear;
    private UUID categoryId;
    private UUID userId;
    private String activeStatus;

    public BookDTO(Book book) {
        this.bid = book.getBid();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.bookStatus = book.getBookStatus();
        this.price = book.getPrice();
        this.qty = book.getQty();
        this.description = book.getDescription();
        this.image = book.getImage();
        this.publishedYear = book.getPublishedYear();
        this.activeStatus = book.getActiveStatus();
        this.categoryId = (book.getCategory() != null) ? book.getCategory().getCid() : null; // Ensure categoryId is set
        this.userId = (book.getUser() != null) ? book.getUser().getUid() : null;
    }

}
