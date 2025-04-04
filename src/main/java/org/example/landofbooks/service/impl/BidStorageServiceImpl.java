package org.example.landofbooks.service.impl;

import jakarta.transaction.Transactional;
import org.example.landofbooks.dto.BidStorageDTO;
import org.example.landofbooks.entity.BidStorage;
import org.example.landofbooks.repo.BidStorageRepo;
import org.example.landofbooks.service.BidStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BidStorageServiceImpl implements BidStorageService {

    @Autowired
    private BidStorageRepo bidStorageRepo;

    @Override
    public List<BidStorageDTO> getBidsByBidId(UUID bidId) {
        List<BidStorage> bids = bidStorageRepo.findByBidding_BidId(bidId);

        return bids.stream()
                .map(bid -> {
                    UUID userId = bid.getUser() != null ? bid.getUser().getUid() : null;
                    UUID biddingId = bid.getBidding() != null ? bid.getBidding().getBidId() : null;
                    return new BidStorageDTO(bid.getBSId(), bid.getMaxPrice(), biddingId, userId);
                })
                .collect(Collectors.toList());
    }

}
