package mobidoc.ci.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import mobidoc.ci.model.enums.UserRole;
import mobidoc.ci.model.enums.UserStatus;

import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    
    @NotBlank
    private String username;
    
    @NotBlank
    @Email
    private String email;
    
    private UserRole role;
    private UserStatus status;
    private UserProfileDTO profile;
}