package com.ptit.backend.mapper;

import com.ptit.backend.dto.request.UserRequest;
import com.ptit.backend.dto.response.UserResponse;
import com.ptit.backend.entity.Role;
import com.ptit.backend.entity.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "role", source = "roleId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserRequest request);

    @Mapping(source = "userId", target = "id")
    @Mapping(source = "role.roleId", target = "roleId")
    UserResponse toResponse(User entity);

    List<UserResponse> toResponseList(List<User> entities);

    default Role mapRole(Long roleId) {
        if (roleId == null) {
            return null;
        }
        return Role.builder().roleId(roleId).build();
    }
}

