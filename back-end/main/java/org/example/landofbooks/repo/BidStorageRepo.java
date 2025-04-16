package org.example.landofbooks.repo;

import org.example.landofbooks.entity.BidStorage;
import org.example.landofbooks.entity.Bidding;
import org.example.landofbooks.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BidStorageRepo extends JpaRepository<BidStorage, UUID> {
    void deleteByBidding_BidId(UUID bidId);
    Optional<BidStorage> findTopByBidding_BidIdOrderByMaxPriceDesc(UUID bidId);

    List<BidStorage> findByBidding_BidId(UUID biddingId);

    Optional<BidStorage> findByBiddingAndUser(Bidding bidding, User user);

    Optional<BidStorage> findFirstByBidding_BidIdOrderByMaxPriceDesc(UUID biddingId);



}
