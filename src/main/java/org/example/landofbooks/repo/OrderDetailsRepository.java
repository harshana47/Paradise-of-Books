package org.example.landofbooks.repo;

import org.example.landofbooks.entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails , UUID> {
}
