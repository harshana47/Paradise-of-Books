package org.example.landofbooks.repo;

import org.example.landofbooks.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BooksRepo extends JpaRepository<Book, UUID> {
    List<Book> findByActiveStatus(String status);

    List<Book> findByUserUidAndActiveStatus(UUID userId, String activeStatus);
}
