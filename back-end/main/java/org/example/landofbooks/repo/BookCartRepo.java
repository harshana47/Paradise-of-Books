package org.example.landofbooks.repo;

import org.example.landofbooks.entity.BookCart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookCartRepo extends JpaRepository<BookCart, UUID> {
    List<BookCart> findByUser_uid(UUID userId);

    void deleteByUser_uid(UUID userId);

    int countByUser_Uid(UUID userId);
}
