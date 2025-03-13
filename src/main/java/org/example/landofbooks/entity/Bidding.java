package org.example.landofbooks.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
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
    private LocalDate bidDate; // Date and time of the bid
    private String image;
    private String author;
    private String title;

    @Column(nullable = false)
    @ColumnDefault("'closed'")
    private String status; // active, closed, won

    // Relationship with User (Each bid is placed by a user)
    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "uid", nullable = false)
    private User user;

    // Relationship with Category (Each bid is linked to a book category)
    @ManyToOne
    @JoinColumn(name = "categoryId", referencedColumnName = "cid", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "bidding", cascade = CascadeType.ALL)
    private List<BidStorage> bidStorages;

    // Automatically set the bid date when creating a new bid
    @PrePersist
    public void setBidDate() {
        this.bidDate = LocalDate.now();
    }
}
