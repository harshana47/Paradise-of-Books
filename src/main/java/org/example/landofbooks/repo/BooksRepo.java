package org.example.landofbooks.repo;

import org.example.landofbooks.entity.Book;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface BooksRepo extends CrudRepository<Book, UUID> {
}
