package org.example.landofbooks.service;

import org.example.landofbooks.dto.BookDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface BookService {
    public boolean addBook(BookDTO bookDTO, MultipartFile image);

    List<BookDTO> getBooksByStatus(String status);

    void updateBookStatus(UUID id, String activeStatus);

    void deleteBook(UUID id);
}
