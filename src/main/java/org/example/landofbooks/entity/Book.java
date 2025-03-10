package org.example.landofbooks.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Entity
@Table(name = "books")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bid;

    private String title;
    private String author;
    private String bookStatus;
    private double price;
    private int qty;
    private String description;
    @Column(name = "image", columnDefinition = "TEXT")
    private String image;
    private String publishedYear;

    @Column(nullable = false)
    @ColumnDefault("'DEACTIVATED'")
    private String activeStatus = "DEACTIVATED";

    @ManyToOne
    @JoinColumn(name = "categoryId", referencedColumnName = "cid")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "uid")
    private User user;

}
