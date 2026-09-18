package com.company.bookhub.role.dto;

import com.company.bookhub.role.entity.Role;

public record RoleResponse(
        Long id,
        String name
) {

    public static RoleResponse from(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getName()
        );
    }
}