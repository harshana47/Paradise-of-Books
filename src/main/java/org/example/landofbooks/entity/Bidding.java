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
    private UUID bidId;

    private double bidAmount;
    private LocalDate bidDate;
    private String image;
    private String author;
    private String title;
    private String description;

    @Column(nullable = false)
    @ColumnDefault("'closed'")
    private String status;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "uid", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "categoryId", referencedColumnName = "cid", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "bidding", cascade = CascadeType.ALL)
    private List<BidStorage> bidStorages;

    @PrePersist
    public void setBidDate() {
        this.bidDate = LocalDate.now();
    }
}
