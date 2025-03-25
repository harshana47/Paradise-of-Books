package org.example.landofbooks.service;


import org.example.landofbooks.dto.UserDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserService {
    int saveUser(UserDTO userDTO);
    UserDTO searchUser(String username);

    List<UserDTO> getAllUsers();

    boolean updateUserRole(UUID userId, String role);
}