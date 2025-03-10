package org.example.landofbooks.controller;

import jakarta.validation.Valid;
import org.example.landofbooks.dto.AuthDTO;
import org.example.landofbooks.dto.CategoryDTO;
import org.example.landofbooks.dto.ResponseDTO;
import org.example.landofbooks.dto.UserDTO;
import org.example.landofbooks.service.UserService;
import org.example.landofbooks.util.JwtUtil;
import org.example.landofbooks.util.VarList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("api/v1/user")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ResponseDTO responseDTO;


    //constructor injection
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
            // Fetch all categories from the service
            List<UserDTO> users = userService.getAllUsers();

            // Set success response
            responseDTO.setCode(200);
            responseDTO.setMessage("User fetched successfully");
            responseDTO.setData(users); // Return the list of categories

            // Return a success response
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } catch (Exception e) {
            // Handle any errors
            responseDTO.setCode(500);
            responseDTO.setMessage("Error fetching Users: " + e.getMessage());
            responseDTO.setData(null);

            // Return an internal server error response
            return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
}
