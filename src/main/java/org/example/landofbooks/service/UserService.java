package org.example.landofbooks.service;


import org.example.landofbooks.dto.UserDTO;

import java.util.List;
import java.util.Optional;


public interface UserService {
    int saveUser(UserDTO userDTO);
    UserDTO searchUser(String username);

    List<UserDTO> getAllUsers();
}