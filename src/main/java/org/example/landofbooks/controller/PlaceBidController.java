package org.example.landofbooks.controller;

import org.example.landofbooks.dto.BiddingDTO;
import org.example.landofbooks.service.BiddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
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
    public ResponseEntity<?> placeBid(@RequestBody BiddingDTO biddingDTO) {
        System.out.println("Received BiddingDTO: " + biddingDTO);  // Log the entire object
        String responseMessage = biddingService.placeBids(biddingDTO);

        Map<String, Object> response = new HashMap<>();
        if (responseMessage.equals("Bid placed successfully!") || responseMessage.equals("Bid updated successfully!")) {
            response.put("status", "success");
            response.put("message", responseMessage);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", responseMessage);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/maxBid/{biddingId}")
    public ResponseEntity<Double> getMaxBid(@PathVariable UUID biddingId) {
        if (biddingId == null) {
            return ResponseEntity.badRequest().build();
        }

        Double maxBid = biddingService.getMaxBid(biddingId);
        return ResponseEntity.ok(maxBid != null ? maxBid : 0.0);
    }

}
