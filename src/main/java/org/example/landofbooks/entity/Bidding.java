package org.example.landofbooks.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bidding")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Bidding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bidId; // Unique identifier for the bid

    private double bidAmount; // Amount the user bids
    private LocalDateTime bidDate; // Date and time of the bid
    private String image;
    private String author;

    @Column(nullable = false)
    private String status; // active, closed, won

    // Relationship with Book (Each bid is for a specific book)
    @ManyToOne
    @JoinColumn(name = "bookId", referencedColumnName = "bid", nullable = false)
    private Book book;

    // Relationship with User (Each bid is placed by a user)
    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "uid", nullable = false)
    private User user;

    // Relationship with Category (Each bid is linked to a book category)
    @ManyToOne
    @JoinColumn(name = "categoryId", referencedColumnName = "cid", nullable = false)
    private Category category;

    // Automatically set the bid date when creating a new bid
    @PrePersist
    public void setBidDate() {
        this.bidDate = LocalDateTime.now();
    }
}
