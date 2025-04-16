package org.example.landofbooks.service.impl;

import org.example.landofbooks.dto.BiddingDTO;
import org.example.landofbooks.dto.BookDTO;
import org.example.landofbooks.entity.Bidding;
import org.example.landofbooks.entity.Book;
import org.example.landofbooks.entity.Category;
import org.example.landofbooks.entity.User;
import org.example.landofbooks.repo.BooksRepo;
import org.example.landofbooks.repo.CategoryRepo;
import org.example.landofbooks.repo.UserRepository;
import org.example.landofbooks.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    @Autowired
    private BooksRepo booksRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public boolean addBook(BookDTO bookDTO, MultipartFile image) {
        try {
            Book book = modelMapper.map(bookDTO, Book.class);

            if (image != null && !image.isEmpty()) {
                String imagePath = saveImage(image);
                if (imagePath != null) {
                    book.setImage(imagePath);
                } else {
                    return false;
                }
            }

            Category category = categoryRepo.findById(bookDTO.getCategoryId()).orElse(null);
            User user = userRepo.findById(bookDTO.getUserId()).orElse(null);

            if (category == null || user == null) {
                return false;
            }

            book.setCategory(category);
            book.setUser(user);

            booksRepo.save(book);
            return true;
        } catch (Exception e) {
            System.err.println("Error while adding book: " + e.getMessage());
            return false;
        }
    }

    public String saveImage(MultipartFile image) {
        try {
            String fileName = UUID.randomUUID().toString();

            String fileExtension = getFileExtension(image);
            if (fileExtension == null) {
                return null;
            }

            String projectRootPath = System.getProperty("user.dir");

            Path path = Paths.get(projectRootPath, "back-end", "main", "resources", "uploads", "images", fileName + fileExtension);

            Files.createDirectories(path.getParent());

            image.transferTo(path.toFile());

            return path.toString();
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
        return null;
    }

    private String getFileExtension(MultipartFile image) {
        String contentType = image.getContentType();
        if (contentType != null) {
            switch (contentType) {
                case "image/jpeg":
                    return ".jpg";
                case "image/png":
                    return ".png";
                case "image/gif":
                    return ".gif";
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public List<BookDTO> getBooksByStatus(String status) {
        List<Book> books = booksRepo.findByActiveStatus(status);
        return books.stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateBookStatus(UUID id, String activeStatus) {
        Book book = booksRepo.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        System.out.println("Old Status: " + book.getActiveStatus());
        System.out.println("New Status: " + activeStatus);

        book.setActiveStatus(activeStatus);
        booksRepo.save(book);

        System.out.println("Updated Status: " + book.getActiveStatus());
    }

    @Override
    public void deleteBook(UUID id) {
        if (!booksRepo.existsById(id)) {
            throw new RuntimeException("Book not found");
        }
        booksRepo.deleteById(id);
    }

    @Override
    public List<BookDTO> getActiveBooksByUserId(UUID userId) {
        return booksRepo.findByUserUidAndActiveStatus(userId, "ACTIVE").stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }

    public List<BookDTO> getActiveBooks() {
        List<Book> books = booksRepo.findByActiveStatus("ACTIVE");
        return books.stream().map(BookDTO::new).collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> getDeactivatedBooksByUserId(UUID userUUID) {
        return booksRepo.findByUserUidAndActiveStatus(userUUID, "DEACTIVATED").stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }

}
