package org.example.landofbooks.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_details")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID odid;

    private double price;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "uid", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "orderId", referencedColumnName = "oid", nullable = false)
    private Orders orders;

    private String title;

    private LocalDateTime orderDate;
}
