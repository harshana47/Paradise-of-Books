package org.example.landofbooks.controller;

import jakarta.validation.Valid;
import org.example.landofbooks.dto.AuthDTO;
import org.example.landofbooks.dto.CategoryDTO;
import org.example.landofbooks.dto.ResponseDTO;
import org.example.landofbooks.dto.UserDTO;
import org.example.landofbooks.entity.User;
import org.example.landofbooks.service.UserService;
import org.example.landofbooks.util.JwtUtil;
import org.example.landofbooks.util.VarList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin("*")
@RequestMapping("api/v1/user")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ResponseDTO responseDTO;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.responseDTO = new ResponseDTO();
    }
    @PostMapping(value = "/register")
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody @Valid UserDTO userDTO) {
        try {
            int res = userService.saveUser(userDTO);
            switch (res) {
                case VarList.Created -> {
                    String token = jwtUtil.generateToken(userDTO);
                    AuthDTO authDTO = new AuthDTO();
                    authDTO.setEmail(userDTO.getEmail());
                    authDTO.setToken(token);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ResponseDTO(VarList.Created, "Success", authDTO));
                }
                case VarList.Not_Acceptable -> {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(new ResponseDTO(VarList.Not_Acceptable, "Email Already Used", null));
                }
                default -> {
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                            .body(new ResponseDTO(VarList.Bad_Gateway, "Error", null));
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }
    @GetMapping
    public ResponseEntity<ResponseDTO> getUser() {
        try {
            List<UserDTO> users = userService.getAllUsers();

            responseDTO.setCode(200);
            responseDTO.setMessage("User fetched successfully");
            responseDTO.setData(users); // Return the list of categories

            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } catch (Exception e) {
            responseDTO.setCode(500);
            responseDTO.setMessage("Error fetching Users: " + e.getMessage());
            responseDTO.setData(null);

            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("users", users); // 👈 Causes issue (nested array)
        return ResponseEntity.ok(response);
    }


    @GetMapping("/findByEmail")
    public ResponseEntity<ResponseDTO> getUserByEmail(@RequestParam String email) {
        try {
            UserDTO user = userService.searchUser(email);
            if (user != null) {
                responseDTO.setCode(200);
                responseDTO.setMessage("User fetched successfully");
                responseDTO.setData(user);
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
            } else {
                responseDTO.setCode(404);
                responseDTO.setMessage("User not found with email: " + email);
                responseDTO.setData(null);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            responseDTO.setCode(500);
            responseDTO.setMessage("Error fetching user by email: " + e.getMessage());
            responseDTO.setData(null);
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/{userId}/role")
    public ResponseEntity<String> updateUserRole(@PathVariable UUID userId, @RequestBody UserDTO userDTO) {
        // Extract role from the received UserDTO
        String newRole = userDTO.getRole();

        // Check if the role is provided
        if (newRole == null || newRole.isEmpty()) {
            return ResponseEntity.badRequest().body("Role must be provided.");
        }

        boolean updated = userService.updateUserRole(userId, newRole);
        if (updated) {
            return ResponseEntity.ok("User role updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to update user role.");
        }
    }
    @PutMapping("update/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") UUID userId, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(userId, userDTO);

        if (updatedUser != null) {
            // Return success and the updated user data
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", updatedUser); // Include updated user data
            return ResponseEntity.ok(response);
        } else {
            // Return failure response with error message
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping("getById/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable("userId") UUID userId) {
        Optional<User> userOptional = userService.getUserById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Convert the user entity to UserDTO before returning
            UserDTO userDTO = new UserDTO();
            userDTO.setUid(user.getUid());
            userDTO.setName(user.getName());
            userDTO.setEmail(user.getEmail());
            userDTO.setContact(user.getContact());
            userDTO.setAddress(user.getAddress());
            userDTO.setRole(user.getRole());

            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }


}
