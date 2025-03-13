package org.example.landofbooks.repo;

import org.example.landofbooks.entity.BidCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BidCartRepo extends JpaRepository<BidCart, UUID> {
}
