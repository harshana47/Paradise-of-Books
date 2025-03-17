package org.example.landofbooks.controller;

import org.example.landofbooks.dto.ResponseDTO;
import org.example.landofbooks.service.BidCartService;
import org.example.landofbooks.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/bidCart")
@CrossOrigin("*")
public class BidCartController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final BidCartService bidCartService;
    private final ResponseDTO responseDTO;

    public BidCartController(JwtUtil jwtUtil, AuthenticationManager authenticationManager, BidCartService bidCartService, ResponseDTO responseDTO) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.bidCartService = bidCartService;
        this.responseDTO = responseDTO;
    }

}
