package org.example.landofbooks.controller;

import org.example.landofbooks.dto.BidCartDTO;
import org.example.landofbooks.service.BidCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bidCart")
@CrossOrigin(origins = "*")
public class BidCartController {

    @Autowired
    private BidCartService bidCartService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BidCartDTO>> getCartByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(bidCartService.getCartByUser(userId));
    }

    @DeleteMapping("/delete/{bcid}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable UUID bcid) {
        bidCartService.deleteCartItem(bcid);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/deleteAll/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable UUID userId) {
        bidCartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/{userId}")
    public int getCartCount(@PathVariable UUID userId) {
        return bidCartService.getBidCartItemCount(userId); // Return the count of cart items
    }
}
