package org.example.landofbooks.controller;

import jakarta.validation.Valid;
import org.example.landofbooks.dto.BookDTO;
import org.example.landofbooks.dto.ResponseDTO;
import org.example.landofbooks.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("api/v1/books")
public class SaleBookController {

    private final BookService bookService;
    private final ResponseDTO responseDTO;

    public SaleBookController(BookService bookService, ResponseDTO responseDTO) {
        this.bookService = bookService;
        this.responseDTO = responseDTO;
    }

    @PostMapping("/place")
    public ResponseEntity<ResponseDTO> addBook(@RequestPart("userId") String userId,
                                               @RequestPart("categoryId") String categoryId,
                                               @RequestPart("bookStatus") String bookStatus,
                                               @RequestPart("author") String author,
                                               @RequestPart("title") String title,
                                               @RequestPart("price") String price,
                                               @RequestPart("description") String description,
                                               @RequestPart("qty") String qty,
                                               @RequestPart("publishedYear") String publishedYear,
                                               @RequestPart("activeStatus") String activeStatus,
                                               @RequestPart(required = false) MultipartFile image) {

        // Create a new BookDTO from the incoming form data
        BookDTO bookDTO = new BookDTO();
        bookDTO.setUserId(UUID.fromString(userId));
        bookDTO.setCategoryId(UUID.fromString(categoryId));
        bookDTO.setBookStatus(bookStatus);
        bookDTO.setAuthor(author);
        bookDTO.setTitle(title);
        bookDTO.setPrice(Double.parseDouble(price));
        bookDTO.setDescription(description);
        bookDTO.setQty(Integer.parseInt(qty));
        bookDTO.setPublishedYear(publishedYear);
        bookDTO.setActiveStatus(activeStatus);

        // Call the service layer to handle the business logic
        boolean isAdded = bookService.addBook(bookDTO, image);  // Pass image as MultipartFile

        if (isAdded) {
            responseDTO.setMessage("Item listed for sale successfully!");
            responseDTO.setData(HttpStatus.CREATED);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } else {
            responseDTO.setMessage("Failed to list item for sale.");
            responseDTO.setData(HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }


    // Get Deactivated Books
    @GetMapping("/byStatus")
    public List<BookDTO> getDeactivatedBooks(@RequestParam String status) {
        return bookService.getBooksByStatus(status);
    }
    @PutMapping("/changeStatus/{id}")
    public ResponseEntity<?> updateBookStatus(@PathVariable UUID id, @RequestBody Map<String, String> request) {
        bookService.updateBookStatus(id, request.get("activeStatus"));
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok().build();
    }



    // Get a list of all books
//    @GetMapping
//    public ResponseEntity<ResponseDTO> getAllBooks() {
//        responseDTO.setData(bookService.getAllBooks());
//        responseDTO.setMessage("Books retrieved successfully.");
//        responseDTO.setStatus(HttpStatus.OK);
//        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
//    }
//
//    // Get book details by ID
//    @GetMapping("/{bookId}")
//    public ResponseEntity<ResponseDTO> getBookById(@PathVariable Long bookId) {
//        BookDTO bookDTO = bookService.getBookById(bookId);
//        if (bookDTO != null) {
//            responseDTO.setData(bookDTO);
//            responseDTO.setMessage("Book details retrieved successfully.");
//            responseDTO.setStatus(HttpStatus.OK);
//            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
//        } else {
//            responseDTO.setMessage("Book not found.");
//            responseDTO.setStatus(HttpStatus.NOT_FOUND);
//            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
//        }
//    }
//
//    // Update a book
//    @PutMapping("/{bookId}")
//    public ResponseEntity<ResponseDTO> updateBook(@PathVariable Long bookId, @Valid @RequestBody BookDTO bookDTO) {
//        boolean isUpdated = bookService.updateBook(bookId, bookDTO);
//        if (isUpdated) {
//            responseDTO.setMessage("Book updated successfully.");
//            responseDTO.setStatus(HttpStatus.OK);
//            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
//        } else {
//            responseDTO.setMessage("Failed to update book.");
//            responseDTO.setStatus(HttpStatus.BAD_REQUEST);
//            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    // Delete a book
//    @DeleteMapping("/{bookId}")
//    public ResponseEntity<ResponseDTO> deleteBook(@PathVariable Long bookId) {
//        boolean isDeleted = bookService.deleteBook(bookId);
//        if (isDeleted) {
//            responseDTO.setMessage("Book deleted successfully.");
//            responseDTO.setStatus(HttpStatus.OK);
//            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
//        } else {
//            responseDTO.setMessage("Failed to delete book.");
//            responseDTO.setStatus(HttpStatus.BAD_REQUEST);
//            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
//        }
//    }
}
