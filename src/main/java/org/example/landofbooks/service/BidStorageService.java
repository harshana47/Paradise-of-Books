package org.example.landofbooks.service;

import org.example.landofbooks.dto.BidStorageDTO;

import java.util.List;
import java.util.UUID;

public interface BidStorageService {
    List<BidStorageDTO> getBidsByBidId(UUID bidId);
}
