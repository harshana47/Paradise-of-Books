package org.example.landofbooks.controller;

import org.example.landofbooks.dto.AuthDTO;
import org.example.landofbooks.dto.BookCartDTO;
import org.example.landofbooks.dto.ResponseDTO;
import org.example.landofbooks.entity.BookCart;
import org.example.landofbooks.service.BookCartService;
import org.example.landofbooks.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookCart")
@CrossOrigin(origins = "*")
public class BookCartController {

    @Autowired
    private BookCartService bookCartService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody BookCartDTO bookCart) {
        try {
            int res = bookCartService.addToCart(bookCart);
            switch (res) {
                case VarList.Created -> {
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ResponseDTO(VarList.Created, "Success", bookCart));
                }
                case VarList.Not_Acceptable -> {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(new ResponseDTO(VarList.Not_Acceptable, "Error", null));
                }
                default -> {
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                            .body(new ResponseDTO(VarList.Bad_Gateway, "Error", null));
                }
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(VarList.Bad_Request, e.getMessage(), null));  // Returning specific error message
        }
    }
}

