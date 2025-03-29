package org.example.landofbooks.service;


import org.example.landofbooks.dto.UserDTO;
import org.example.landofbooks.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserService {
    int saveUser(UserDTO userDTO);
    UserDTO searchUser(String username);

    List<UserDTO> getAllUsers();

    boolean updateUserRole(UUID userId, String role);

    boolean resetPassword(String email, String password);

    UserDTO updateUser(UUID userId, UserDTO userDTO);

    public Optional<User> getUserById(UUID userId);
}