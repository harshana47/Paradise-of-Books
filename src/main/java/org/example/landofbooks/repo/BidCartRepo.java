package org.example.landofbooks.repo;

import org.example.landofbooks.entity.BidCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BidCartRepo extends JpaRepository<BidCart, UUID> {
    void deleteByUser_uid(UUID userId);
    List<BidCart> findByUser_uid(UUID userId);

    int countByUser_Uid(UUID userId);
}
