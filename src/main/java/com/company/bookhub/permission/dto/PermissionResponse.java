package com.company.bookhub.permission.dto;

import com.company.bookhub.permission.entity.Permission;

public record PermissionResponse(
        Long id,
        String name
) {

    public static PermissionResponse from(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getName()
        );
    }
}