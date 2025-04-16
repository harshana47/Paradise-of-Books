package org.example.landofbooks.repo;

import org.example.landofbooks.entity.Bidding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface BiddingRepo extends JpaRepository<Bidding, UUID> {
    List<Bidding> findByStatus(String status);

    List<Bidding> findByUserUidAndStatus(UUID userId, String active);
}
