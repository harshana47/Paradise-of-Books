package org.example.landofbooks.repo;

import org.example.landofbooks.entity.BidStorage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BidStorageRepo extends JpaRepository<BidStorage, UUID> {
    void deleteByBidding_BidId(UUID bidId);
    Optional<BidStorage> findTopByBidding_BidIdOrderByMaxPriceDesc(UUID bidId);

    List<BidStorage> findByBidding_BidId(UUID biddingId);
}
