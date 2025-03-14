package org.example.landofbooks.controller;

import org.example.landofbooks.dto.BiddingDTO;
import org.example.landofbooks.service.BiddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bidStorage")
public class PlaceBidController {

    private final BiddingService biddingService;

    @Autowired
    public PlaceBidController(BiddingService biddingService) {
        this.biddingService = biddingService;
    }

    @PostMapping("/placeBid")
    public ResponseEntity<String> placeBid(@RequestBody BiddingDTO biddingDTO) {
        String responseMessage = biddingService.placeBids(biddingDTO);

        if (responseMessage.equals("Bid placed successfully!") || responseMessage.equals("Bid updated successfully!")) {
            return ResponseEntity.ok(responseMessage);
        } else {
            return ResponseEntity.badRequest().body(responseMessage);
        }
    }
    @GetMapping("/maxBid/{biddingId}")
    public ResponseEntity<Double> getMaxBid(@PathVariable UUID biddingId) {
        if (biddingId == null) {
            return ResponseEntity.badRequest().build();  // Return 400 Bad Request if UUID is null
        }

        Double maxBid = biddingService.getMaxBid(biddingId);

        return ResponseEntity.ok(maxBid != null ? maxBid : 0.0); // Ensure it always returns a number
    }

}
