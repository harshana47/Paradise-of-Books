package org.example.landofbooks.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID oid;

    private double totalPrice;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "uid", nullable = false)
    private User user;
    private String address;
    private String contact;
    @ColumnDefault("'INCOMPLETE'")
    private String status;

}