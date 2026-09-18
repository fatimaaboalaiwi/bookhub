package com.company.bookhub.role.dto;

import com.company.bookhub.permission.dto.PermissionResponse;
import com.company.bookhub.role.entity.Role;

import java.util.Set;
import java.util.stream.Collectors;

public record RoleResponse(
        Long id,
        String name,
        Set<PermissionResponse> permissions
) {

    public static RoleResponse from(Role role) {

        Set<PermissionResponse> rolePermissions = role.getPermissions()
                .stream()
                .map(PermissionResponse::from)
                .collect(Collectors.toSet());

        return new RoleResponse(
                role.getId(),
                role.getName(),
                rolePermissions

        );
    }
}