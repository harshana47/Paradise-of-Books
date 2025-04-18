package org.example.landofbooks.service;

import org.example.landofbooks.dto.BidStorageDTO;
import org.example.landofbooks.dto.BiddingDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface BiddingService {
    public boolean placeBid(BiddingDTO biddingDTO, MultipartFile image);

    List<BiddingDTO> getBidByStatus(String status);

    void updateBidStatus(UUID id, String status);

    void deleteBid(UUID id);

    Object getOngoingBidsByUser(UUID userId);

    void deleteStor(UUID bidId);

    public boolean endBid(UUID bidId);

    List<BiddingDTO> getActiveBids();

    String placeBids(BiddingDTO biddingDTO);

    Double getMaxBid(UUID biddingId);

    Object getPendingBidsByUser(UUID userId);
}
