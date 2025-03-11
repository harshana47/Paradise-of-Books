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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

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

    @Override
    public boolean addBook(BookDTO bookDTO) {
        try {
            // Convert the BookDTO to Book entity
            Book book = modelMapper.map(bookDTO, Book.class);

            // If book image is provided as Base64, process and save it
            if (bookDTO.getImage() != null) {
                String imagePath = saveImage(bookDTO.getImage()); // Save the image and get the path
                book.setImage(imagePath); // Set the image path in the book entity
            }
            Category category = categoryRepo.findById(bookDTO.getCategoryId()).orElse(null);
            User user = userRepo.findById(bookDTO.getUserId()).orElse(null);
            book.setCategory(category);
            book.setUser(user);

            // Save the book in the repository
            booksRepo.save(book);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String saveImage(String base64Image) {
        try {
            // Remove any extra characters (e.g., spaces) that may have been added during transfer
            String sanitizedBase64 = base64Image.replaceAll("[^A-Za-z0-9+/=]", "");

            // Decode the image
            byte[] decodedImage = Base64.getDecoder().decode(sanitizedBase64);

            // Generate a unique file name using UUID
            String fileName = UUID.randomUUID().toString() + ".jpg"; // You can change the extension if needed

            // Define the directory where the image will be saved (adjust path as needed)
            Path path = Paths.get("uploads","images", fileName);

            // Ensure the directory exists
            Files.createDirectories(path.getParent());

            // Save the image to the file system
            Files.write(path, decodedImage);

            // Return the image path (relative or absolute based on your requirement)
            return path.toString();
        } catch (IllegalArgumentException e) {
            System.err.println("Error decoding base64 string: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
        return null; // Return null if there was an error
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
