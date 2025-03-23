package org.example.landofbooks.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "bookCart")
public class BookCart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bkcid;

    @ManyToOne
    @JoinColumn(name = "bookId", referencedColumnName = "bid")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "uid")
    private User user;

    private double price;
    private String title;
    private String image;
    private double total;
    private int qty;
}

