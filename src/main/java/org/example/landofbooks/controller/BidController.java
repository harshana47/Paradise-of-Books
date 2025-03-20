package org.example.landofbooks.controller;

import org.example.landofbooks.dto.BidStorageDTO;
import org.example.landofbooks.dto.BiddingDTO;
import org.example.landofbooks.dto.ResponseDTO;
import org.example.landofbooks.entity.Bidding;
import org.example.landofbooks.service.BidStorageService;
import org.example.landofbooks.service.BiddingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("api/v1/bidding")
public class BidController {

    private final BiddingService biddingService;
    private final ResponseDTO responseDTO;
    private final BidStorageService bidStorageService;

    public BidController(BiddingService biddingService, ResponseDTO responseDTO, BidStorageService bidStorageService) {
        this.biddingService = biddingService;
        this.responseDTO = responseDTO;
        this.bidStorageService = bidStorageService;
    }

    @PostMapping("/place")
    public ResponseEntity<ResponseDTO> placeBid(@RequestPart("userId") String userId,
                                                @RequestPart("categoryId") String categoryId,
                                                @RequestPart("bidAmount") String bidAmount,
                                                @RequestPart("author") String author,
                                                @RequestPart("title") String title,
                                                @RequestPart("description") String description,
                                                @RequestPart("bidDate") String bidDate,
                                                @RequestPart("status") String status,
                                                @RequestPart(required = false) MultipartFile image) {

        BiddingDTO biddingDTO = new BiddingDTO();
        biddingDTO.setUserId(UUID.fromString(userId));
        biddingDTO.setCategoryId(UUID.fromString(categoryId));
        biddingDTO.setBidAmount(Double.parseDouble(bidAmount));
        biddingDTO.setDescription(description);
        biddingDTO.setAuthor(author);
        biddingDTO.setTitle(title);
        biddingDTO.setBidDate(LocalDateTime.parse(bidDate));
        biddingDTO.setStatus(status);

        boolean isAdded = biddingService.placeBid(biddingDTO,image);
        if (isAdded) {
            responseDTO.setMessage("Item listed for sale successfully!");
            responseDTO.setData(HttpStatus.CREATED);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } else {
            responseDTO.setMessage("Failed to list item for sale.");
            responseDTO.setData(HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/byStatus")
    public List<BiddingDTO> getClosedBids(@RequestParam String status) {
        return biddingService.getBidByStatus(status);
    }
    @PutMapping("/changeStatus/{id}")
    public ResponseEntity<Map<String, String>> updateBidStatus(@PathVariable UUID id, @RequestBody Map<String, String> request) {
        System.out.println("Received update request: " + request);
        biddingService.updateBidStatus(id, request.get("status"));
        Map<String, String> response = new HashMap<>();
        response.put("message", "Status updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBid(@PathVariable UUID id) {
        biddingService.deleteBid(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/byUser")
    public ResponseEntity<?> getOngoingBidsByUser(@RequestParam UUID userId) {
        return ResponseEntity.ok(biddingService.getOngoingBidsByUser(userId));
    }
    @DeleteMapping("/deleteStorage/{bidId}")
    public ResponseEntity<?> deleteBidStorage(@PathVariable UUID bidId) {
        biddingService.deleteStor(bidId);
        return ResponseEntity.ok("Bid storage deleted successfully.");
    }
    @PostMapping("/end/{bidId}")
    public ResponseEntity<?> endBid(@PathVariable UUID bidId) {
        try {
            boolean isBidEnded = biddingService.endBid(bidId);

            if (isBidEnded) {
                return ResponseEntity.ok("Bid successfully moved to the highest bidder’s cart and closed.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bid not found or unable to close.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the bid.");
        }
    }

    @GetMapping("/bids/{bidId}")
    public ResponseEntity<List<BidStorageDTO>> getBidsByBidId(@PathVariable String bidId) {
        UUID bidUUID = UUID.fromString(bidId);
        List<BidStorageDTO> bids = bidStorageService.getBidsByBidId(bidUUID);
        return ResponseEntity.ok(bids);
    }

    @GetMapping("/active")
    public List<BiddingDTO> getActiveBids() {
        return biddingService.getActiveBids();
    }
}
