package org.example.landofbooks.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "bidCart")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BidCart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bcid;

    @ManyToOne
    @JoinColumn(name = "biddingId", referencedColumnName = "bidId")
    private Bidding bidding;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "uid")
    private User user;

    private double maxPrice;

    private String title;
}
