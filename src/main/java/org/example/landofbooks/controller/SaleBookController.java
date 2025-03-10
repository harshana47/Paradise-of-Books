package org.example.landofbooks.controller;

import jakarta.validation.Valid;
import org.example.landofbooks.dto.BookDTO;
import org.example.landofbooks.dto.ResponseDTO;
import org.example.landofbooks.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // Add a new book
    @PostMapping("/place")
    public ResponseEntity<ResponseDTO> addBook(@Valid @RequestBody BookDTO bookDTO) {
        boolean isAdded = bookService.addBook(bookDTO);
        if (isAdded) {
            responseDTO.setMessage("Book added successfully!");
            responseDTO.setData(HttpStatus.CREATED);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } else {
            responseDTO.setMessage("Failed to add book.");
            responseDTO.setData(HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
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
