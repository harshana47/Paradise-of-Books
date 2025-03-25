package org.example.landofbooks.service;

import org.example.landofbooks.dto.BidCartDTO;

import java.util.List;
import java.util.UUID;

public interface BidCartService {
    public void clearCart(UUID userId);
    public void deleteCartItem(UUID bcid);
    public List<BidCartDTO> getCartByUser(UUID userId);

    int getBidCartItemCount(UUID userId);
}
