package org.example.landofbooks.repo;

import org.example.landofbooks.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Orders , UUID> {
}
