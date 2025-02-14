package mobidoc.ci.service;

import mobidoc.ci.dto.UserDTO;
import mobidoc.ci.model.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(UUID id, UserDTO userDTO);
    UserDTO getUserById(UUID id);
    UserDTO getUserByUsername(String username);
    Page<UserDTO> getAllUsers(Pageable pageable);
    Page<UserDTO> getUsersByRole(UserRole role, Pageable pageable);
    void deleteUser(UUID id);
    void updateUserStatus(UUID id, String status);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}