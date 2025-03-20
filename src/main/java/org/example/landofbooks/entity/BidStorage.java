package org.example.landofbooks.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "bidStorage")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BidStorage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bSId;

    private double maxPrice;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) // add cascade here
    @JoinColumn(name = "biddingId", referencedColumnName = "bidId")
    private Bidding bidding;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "uid")
    private User user;
}
