package org.example.landofbooks.repo;

import org.example.landofbooks.entity.Book;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface BooksRepo extends CrudRepository<Book, UUID> {
    List<Book> findByActiveStatus(String status);
}
