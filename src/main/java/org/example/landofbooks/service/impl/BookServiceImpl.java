package org.example.landofbooks.service.impl;

import org.example.landofbooks.dto.BookDTO;
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
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:63342")
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

    public boolean addBook(BookDTO bookDTO, MultipartFile image) {
        try {
            // Convert the BookDTO to Book entity
            Book book = modelMapper.map(bookDTO, Book.class);

            // If book image is provided as a file, process and save it
            if (image != null && !image.isEmpty()) {
                String imagePath = saveImage(image); // Save the image and get the relative path
                if (imagePath != null) {
                    book.setImage(imagePath); // Set the image path in the book entity
                } else {
                    // If image saving fails, return false
                    return false;
                }
            }

            // Retrieve Category and User entities
            Category category = categoryRepo.findById(bookDTO.getCategoryId()).orElse(null);
            User user = userRepo.findById(bookDTO.getUserId()).orElse(null);

            // If Category or User is not found, return false
            if (category == null || user == null) {
                return false;
            }

            // Set Category and User in the Book entity
            book.setCategory(category);
            book.setUser(user);

            // Save the book in the repository
            booksRepo.save(book);
            return true;
        } catch (Exception e) {
            // Log the exception (you can use a logging framework like SLF4J)
            System.err.println("Error while adding book: " + e.getMessage());
            return false;
        }
    }

    public String saveImage(MultipartFile image) {
        try {
            // Generate a unique file name using UUID
            String fileName = UUID.randomUUID().toString();

            // Get file extension based on the original file content type
            String fileExtension = getFileExtension(image);
            if (fileExtension == null) {
                // Return null if the file type is not supported
                return null;
            }

            // Get the absolute path of the project directory
            String projectRootPath = System.getProperty("user.dir");

            // Define the absolute path where the image will be saved (inside the project)
            Path path = Paths.get(projectRootPath, "uploads", "images", fileName + fileExtension);

            // Ensure the directory exists
            Files.createDirectories(path.getParent());  // Create parent directories if they don't exist

            // Save the image to the file system
            image.transferTo(path.toFile());  // Save the file

            // Return the relative path (you can store this in the database)
            return path.toString();  // Return the file path relative to your project root
        } catch (IOException e) {
            // Log the error (consider using a logging framework)
            System.err.println("Error saving image: " + e.getMessage());
        }
        return null;  // Return null if there was an error
    }

    // Helper method to get file extension
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
                    return null;  // Unsupported file type
            }
        }
        return null;  // Return null if contentType is null
    }



    // Get Books by Status (e.g., DEACTIVATED)
    @Override
    public List<BookDTO> getBooksByStatus(String status) {
        List<Book> books = booksRepo.findByActiveStatus(status);  // Fetch from DB
        return books.stream()
                .map(book -> modelMapper.map(book, BookDTO.class))  // Convert Entity to DTO
                .collect(Collectors.toList());
    }

    // Update Book Status (e.g., Change to ACTIVE)
    @Override
    public void updateBookStatus(UUID id, String activeStatus) {
        Book book = booksRepo.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        book.setActiveStatus(activeStatus);  // Update status
        booksRepo.save(book);  // Save changes
    }

    // Delete Book by UUID
    @Override
    public void deleteBook(UUID id) {
        if (!booksRepo.existsById(id)) {
            throw new RuntimeException("Book not found");
        }
        booksRepo.deleteById(id);
    }

//    @Override
//    public BookDTO getBookById(Long bookId) {
//        Book book = booksRepo.findById(bookId).orElse(null);
//        if (book != null) {
//            return modelMapper.map(book, BookDTO.class);
//        }
//        return null;
//    }
//
//    @Override
//    public Iterable<BookDTO> getAllBooks() {
//        Iterable<Book> books = booksRepo.findAll();
//        return modelMapper.map(books, Iterable.class);
//    }
//
//    @Override
//    public boolean updateBook(Long bookId, BookDTO bookDTO) {
//        try {
//            Book existingBook = booksRepo.findById(bookId).orElse(null);
//            if (existingBook != null) {
//                // Update book fields
//                existingBook.setTitle(bookDTO.getTitle());
//                existingBook.setAuthor(bookDTO.getAuthor());
//                existingBook.setPrice(bookDTO.getPrice());
//                existingBook.setDescription(bookDTO.getDescription());
//
//                // If book image is provided, process and update it
//                if (bookDTO.getImage() != null) {
//                    String imagePath = saveImage(bookDTO.getImage());
//                    existingBook.setImage(imagePath);
//                }
//
//                // Save the updated book
//                booksRepo.save(existingBook);
//                return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    @Override
//    public boolean deleteBook(Long bookId) {
//        try {
//            Book book = booksRepo.findById(bookId).orElse(null);
//            if (book != null) {
//                booksRepo.delete(book);
//                return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }


}
