package com.company.bookhub.permission.dto;

import com.company.bookhub.permission.entity.Permission;

public record PermissionResponse(
        Long id,
        String name,
        boolean systemPermission
) {

    public static PermissionResponse from(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getName(),
                permission.isSystemPermission()
        );
    }
}