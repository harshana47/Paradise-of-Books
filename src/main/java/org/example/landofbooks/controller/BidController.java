package org.example.landofbooks.controller;

import org.example.landofbooks.dto.BiddingDTO;
import org.example.landofbooks.dto.ResponseDTO;
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

    // Constructor injection for BiddingService and ResponseDTO
    public BidController(BiddingService biddingService, ResponseDTO responseDTO) {
        this.biddingService = biddingService;
        this.responseDTO = responseDTO;
    }

    // Endpoint to create a new bid
    @PostMapping("/place")
    public ResponseEntity<ResponseDTO> placeBid(@RequestPart("userId") String userId,
                                                @RequestPart("categoryId") String categoryId,
                                                @RequestPart("bidAmount") String bidAmount,
                                                @RequestPart("author") String author,
                                                @RequestPart("title") String title,
                                                @RequestPart("bidDate") String bidDate,
                                                @RequestPart("status") String status,
                                                @RequestPart(required = false) MultipartFile image) {

        BiddingDTO biddingDTO = new BiddingDTO();
        biddingDTO.setUserId(UUID.fromString(userId));
        biddingDTO.setCategoryId(UUID.fromString(categoryId));
        biddingDTO.setBidAmount(Double.parseDouble(bidAmount));
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
    // Endpoint to get the list of all bids for a specific book
//    @GetMapping("/book/{bookId}")
//    public ResponseEntity<ResponseDTO> getBidsForBook(@PathVariable("bookId") Long bookId) {
//        try {
//            // Fetch the bids for the given bookId
//            var bids = biddingService.getBidsForBook(bookId);
//            if (bids != null && !bids.isEmpty()) {
//                // Return a successful response with the bids list
//                responseDTO.setMessage("Bids fetched successfully");
//                responseDTO.setStatus(HttpStatus.OK.value());
//                responseDTO.setData(bids);
//                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
//            } else {
//                // Return a response indicating no bids found
//                responseDTO.setMessage("No bids found for this book");
//                responseDTO.setStatus(HttpStatus.NOT_FOUND.value());
//                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
//            }
//        } catch (Exception e) {
//            // Handle any unexpected errors
//            responseDTO.setMessage("Error fetching bids: " + e.getMessage());
//            responseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    // Endpoint to get the details of a specific bid
//    @GetMapping("/{bidId}")
//    public ResponseEntity<ResponseDTO> getBidDetails(@PathVariable("bidId") Long bidId) {
//        try {
//            // Fetch the bid details by bidId
//            var bid = biddingService.getBidDetails(bidId);
//            if (bid != null) {
//                // Return a successful response with the bid details
//                responseDTO.setMessage("Bid details fetched successfully");
//                responseDTO.setStatus(HttpStatus.OK.value());
//                responseDTO.setData(bid);
//                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
//            } else {
//                // Return a response indicating the bid wasn't found
//                responseDTO.setMessage("Bid not found");
//                responseDTO.setStatus(HttpStatus.NOT_FOUND.value());
//                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
//            }
//        } catch (Exception e) {
//            // Handle any unexpected errors
//            responseDTO.setMessage("Error fetching bid details: " + e.getMessage());
//            responseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    // Endpoint to update an existing bid
//    @PutMapping("/{bidId}/update")
//    public ResponseEntity<ResponseDTO> updateBid(@PathVariable("bidId") Long bidId, @RequestBody @Valid BiddingDTO biddingDTO) {
//        try {
//            // Call the service to update the bid
//            boolean isUpdated = biddingService.updateBid(bidId, biddingDTO);
//            if (isUpdated) {
//                // Return a successful response
//                responseDTO.setMessage("Bid updated successfully");
//                responseDTO.setStatus(HttpStatus.OK.value());
//                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
//            } else {
//                // Return a failed response indicating the update was unsuccessful
//                responseDTO.setMessage("Failed to update the bid");
//                responseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
//                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
//            }
//        } catch (Exception e) {
//            // Handle any unexpected errors
//            responseDTO.setMessage("Error updating bid: " + e.getMessage());
//            responseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    // Endpoint to delete a bid
//    @DeleteMapping("/{bidId}/delete")
//    public ResponseEntity<ResponseDTO> deleteBid(@PathVariable("bidId") Long bidId) {
//        try {
//            // Call the service to delete the bid
//            boolean isDeleted = biddingService.deleteBid(bidId);
//            if (isDeleted) {
//                // Return a successful response
//                responseDTO.setMessage("Bid deleted successfully");
//                responseDTO.setStatus(HttpStatus.NO_CONTENT.value());
//                return new ResponseEntity<>(responseDTO, HttpStatus.NO_CONTENT);
//            } else {
//                // Return a failed response indicating the deletion failed
//                responseDTO.setMessage("Failed to delete the bid");
//                responseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
//                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
//            }
//        } catch (Exception e) {
//            // Handle any unexpected errors
//            responseDTO.setMessage("Error deleting bid: " + e.getMessage());
//            responseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
