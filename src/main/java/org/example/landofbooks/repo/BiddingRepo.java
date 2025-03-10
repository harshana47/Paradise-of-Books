package org.example.landofbooks.repo;

import org.example.landofbooks.entity.Bidding;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface BiddingRepo extends CrudRepository<Bidding, UUID> {
}
