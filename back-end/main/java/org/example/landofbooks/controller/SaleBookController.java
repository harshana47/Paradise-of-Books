package org.example.landofbooks.controller;

import jakarta.validation.Valid;
import org.example.landofbooks.dto.BiddingDTO;
import org.example.landofbooks.dto.BookDTO;
import org.example.landofbooks.dto.ResponseDTO;
import org.example.landofbooks.entity.Book;
import org.example.landofbooks.service.BookService;
import org.example.landofbooks.service.EmailService;
import org.example.landofbooks.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:63342")
@RequestMapping("api/v1/books")
public class SaleBookController {

    private final BookService bookService;
    private final ResponseDTO responseDTO;
    private final UserService userService;
    private final EmailService emailService;

    public SaleBookController(BookService bookService, ResponseDTO responseDTO, UserService userService, EmailService emailService) {
        this.bookService = bookService;
        this.responseDTO = responseDTO;
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/place")
    public ResponseEntity<ResponseDTO> addBook(@Valid
                                                   @RequestPart("userId") String userId,
                                               @RequestPart("categoryId") String categoryId,
                                               @RequestPart("bookStatus") String bookStatus,
                                               @RequestPart("author") String author,
                                               @RequestPart("title") String title,
                                               @RequestPart("price") String price,
                                               @RequestPart("description") String description,
                                               @RequestPart("qty") String qty,
                                               @RequestPart("publishedYear") String publishedYear,
                                               @RequestPart("activeStatus") String activeStatus,
                                               @RequestPart("email") String email,
                                               @RequestPart(required = false) MultipartFile image) {

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

        boolean isAdded = bookService.addBook(bookDTO, image);

        if (isAdded) {
            responseDTO.setMessage("Item listed for sale successfully!");
            emailService.sendSuccessEmail(email,title);
            responseDTO.setData(HttpStatus.CREATED);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } else {
            responseDTO.setMessage("Failed to list item for sale.");
            responseDTO.setData(HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/byStatus")
    public List<BookDTO> getDeactivatedBooks(@RequestParam String status) {
        return bookService.getBooksByStatus(status);
    }
    @PutMapping("/changeStatus/{id}")
    public ResponseEntity<Map<String, String>> updateBookStatus(@PathVariable UUID id, @RequestBody Map<String, String> request) {
        bookService.updateBookStatus(id, request.get("activeStatus"));
        Map<String, String> response = new HashMap<>();
        response.put("message", "Status updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/sales")
    public List<BookDTO> getActiveBooks(@RequestParam String userId) {
        UUID userUUID = UUID.fromString(userId);
        return bookService.getActiveBooksByUserId(userUUID);
    }
    @GetMapping("/ACTIVE")
    public List<BookDTO> getActiveBids() {
        return bookService.getActiveBooks();
    }
    @GetMapping("/pending")
    public List<BookDTO> getPendingBooks(@RequestParam String userId) {
        UUID userUUID = UUID.fromString(userId);
        return bookService.getDeactivatedBooksByUserId(userUUID);
    }
}
