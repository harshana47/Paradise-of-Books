package org.example.landofbooks.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    private UUID uid;
    @Email(message = "Invalid Email Address")
    private String email;
    private String password;
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[A-Za-z ]+$",message = "Name must contain only letters and spaces")
    @Size(min = 3,max = 200,message = "name should be 3-200 letters")
    private String name;
    private String role;
    @Pattern(regexp = "^[0-9]{10}$",message = "Phone number must be exactly 10 digits")
    private String contact;
    private String address;
}
