package org.example.landofbooks.service;

import org.example.landofbooks.dto.BiddingDTO;
import org.springframework.web.multipart.MultipartFile;

public interface BiddingService {
    public boolean placeBid(BiddingDTO biddingDTO, MultipartFile image);
}
