package com.company.bookhub.user.dto;

import com.company.bookhub.role.dto.RoleResponse;
import com.company.bookhub.role.entity.Role;
import com.company.bookhub.user.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

public record UserResponse(
        Long id,
        String username,
        boolean systemUser,
        Set<RoleResponse> roles
) {
    public static UserResponse from(User user) {

        Set<RoleResponse> roleResponses = user.getRoles()
                .stream()
                .map(RoleResponse::from)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.isSystemUser(),
                roleResponses
        );
    }
}
