package org.example.landofbooks.repo;

import org.example.landofbooks.entity.BookCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookCartRepo extends JpaRepository<BookCart, UUID> {
}
