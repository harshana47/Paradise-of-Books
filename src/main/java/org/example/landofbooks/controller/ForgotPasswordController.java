//package org.example.landofbooks.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.example.landofbooks.entity.OtpStore;
//import org.example.landofbooks.entity.User;
//import org.example.landofbooks.repo.UserRepository;
//import org.example.landofbooks.service.EmailService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/v1/auth")
//@RequiredArgsConstructor
//public class ForgotPasswordController {
//
//    private final UserRepository userRepository;
//    private final EmailService emailService;
//    private final OtpStore otpStore;
//
//    @PostMapping("/forgot-password")
//    public ResponseEntity<?> forgotPassword(@RequestBody User userRequest) {
//        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(userRequest.getEmail()));
//        if (user.isEmpty()) {
//            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"Email not found\"}");
//        }
//
//        String otp = emailService.generateOTP();
//        otpStore.storeOTP(userRequest.getEmail(), otp);
//        emailService.sendEmail(userRequest.getEmail(), "Your OTP Code", "Your OTP is: " + otp);
//
//        return ResponseEntity.ok("{\"success\": true, \"message\": \"OTP sent to email\"}");
//    }
//}
