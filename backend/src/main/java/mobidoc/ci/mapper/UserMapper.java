package mobidoc.ci.mapper;

import mobidoc.ci.dto.UserDTO;
import mobidoc.ci.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "status", source = "status")
    UserDTO toDto(User user);

    @InheritInverseConfiguration
    @Mapping(target = "keycloakId", ignore = true)
    @Mapping(target = "profile", ignore = true)
    User toEntity(UserDTO userDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserDTO userDTO, @MappingTarget User user);
}