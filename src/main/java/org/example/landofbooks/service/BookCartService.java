package org.example.landofbooks.service;

import org.example.landofbooks.dto.BookCartDTO;
import org.example.landofbooks.entity.BookCart;

public interface BookCartService {
    int addToCart(BookCartDTO bookCart);
}
