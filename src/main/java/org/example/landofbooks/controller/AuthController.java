package org.example.landofbooks.controller;

import org.example.landofbooks.dto.AuthDTO;
import org.example.landofbooks.dto.ResponseDTO;
import org.example.landofbooks.dto.UserDTO;
import org.example.landofbooks.entity.OtpStore;
import org.example.landofbooks.entity.User;
import org.example.landofbooks.repo.UserRepository;
import org.example.landofbooks.service.EmailService;
import org.example.landofbooks.service.OtpService;
import org.example.landofbooks.service.impl.UserServiceImpl;
import org.example.landofbooks.util.JwtUtil;
import org.example.landofbooks.util.VarList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/auth")
@CrossOrigin("*")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;
    private final ResponseDTO responseDTO;

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final OtpStore otpStore;
    private final OtpService otpService;

    public AuthController(JwtUtil jwtUtil, AuthenticationManager authenticationManager, UserServiceImpl userService, ResponseDTO responseDTO, UserRepository userRepository, EmailService emailService, OtpStore otpStore, OtpService otpService) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.responseDTO = responseDTO;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.otpStore = otpStore;
        this.otpService = otpService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody User userRequest) {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(userRequest.getEmail()));

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email not found"));
        }

        String otp = emailService.generateOTP();
        otpService.storeOtp(userRequest.getEmail(), otp);
        emailService.sendOtpEmail(userRequest.getEmail());

        return ResponseEntity.ok(Map.of("success", true, "message", "OTP sent to email"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        // Assuming otpService.verifyOtp is implemented correctly
        boolean isValid = otpService.verifyOtp(email, otp);

        if (isValid) {
            return ResponseEntity.ok(Map.of("success", true, "message", "OTP verified successfully. You can now reset your password."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid or expired OTP."));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        // Call your service method to reset the password here
        boolean isResetSuccessful = userService.resetPassword(email, password);

        if (isResetSuccessful) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Password reset successfully."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Error resetting password."));
        }
    }


    @PostMapping("/authenticate")
    public ResponseEntity<ResponseDTO> authenticate(@RequestBody UserDTO userDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO(VarList.Unauthorized, "Invalid Credentials", e.getMessage()));
        }

        UserDTO loadedUser = userService.loadUserDetailsByUsername(userDTO.getEmail());
        if (loadedUser == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO(VarList.Conflict, "Authorization Failure! Please Try Again", null));
        }

        String token = jwtUtil.generateToken(loadedUser);
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseDTO(VarList.Conflict, "Authorization Failure! Please Try Again", null));
        }

        AuthDTO authDTO = new AuthDTO();
        authDTO.setEmail(loadedUser.getEmail());
        authDTO.setToken(token);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO(VarList.Created, "Success", authDTO));
    }
}

