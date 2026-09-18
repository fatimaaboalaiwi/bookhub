package com.company.bookhub.role.service;

import com.company.bookhub.permission.dto.PermissionResponse;

import java.util.List;

public interface RolePermissionService {

    void manageRolePermissions(
            Long roleId,
            List<Long> permissionIds
    );

    List<PermissionResponse> getRolePermissions(
            Long roleId
    );
}