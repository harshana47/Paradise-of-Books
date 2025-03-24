package org.example.landofbooks.service;

import org.example.landofbooks.dto.BookCartDTO;
import org.example.landofbooks.entity.BookCart;

import java.util.List;
import java.util.UUID;

public interface BookCartService {
    int addToCart(BookCartDTO bookCart);

    List<BookCartDTO> getCartByUser(UUID userId);

    void deleteCartItem(UUID bkcid);

    void clearCart(UUID userId);
}
