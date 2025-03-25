package org.example.landofbooks.service.impl;

import org.example.landofbooks.dto.BookCartDTO;
import org.example.landofbooks.entity.Book;
import org.example.landofbooks.entity.BookCart;
import org.example.landofbooks.entity.User;
import org.example.landofbooks.repo.BookCartRepo;
import org.example.landofbooks.repo.BooksRepo;
import org.example.landofbooks.repo.UserRepository;
import org.example.landofbooks.service.BookCartService;
import org.example.landofbooks.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookCartServiceImpl implements BookCartService {

    @Autowired
    private BookCartRepo bookCartRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BooksRepo booksRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public int addToCart(BookCartDTO bookCartDTO) {
        // Fetch the Book and User entities based on the provided IDs
        Book book = booksRepo.findById(bookCartDTO.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepo.findById(bookCartDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the quantity is available in the book inventory
        if (book.getQty() < bookCartDTO.getQty()) {
            throw new RuntimeException("Insufficient quantity available for the book: " + book.getTitle());
        }

        // Decrease the quantity of the book by the quantity added to the cart
        book.setQty(book.getQty() - bookCartDTO.getQty());

        // Save the updated Book entity to reflect the reduced quantity
        booksRepo.save(book);

        // Map the DTO to the entity and set the Book and User relationships
        BookCart bookCart = modelMapper.map(bookCartDTO, BookCart.class);
        bookCart.setBook(book);  // Set the Book entity
        bookCart.setUser(user);  // Set the User entity

        // Save the BookCart entity
        bookCartRepo.save(bookCart);

        return VarList.Created;
    }

    @Override
    public List<BookCartDTO> getCartByUser(UUID userId) {
        List<BookCart> cartList = bookCartRepo.findByUser_uid(userId);
        return cartList.stream()
                .map(item -> modelMapper.map(item, BookCartDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCartItem(UUID bkcid) {
        bookCartRepo.deleteById(bkcid);
    }

    @Override
    public void clearCart(UUID userId) {
        bookCartRepo.deleteByUser_uid(userId);
    }

    public int getBookCartItemCount(UUID userId) {
        return bookCartRepo.countByUser_Uid(userId); // Use the correct property path
    }
}
