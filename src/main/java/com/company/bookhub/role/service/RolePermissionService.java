package com.company.bookhub.role.service;

import com.company.bookhub.permission.dto.PermissionResponse;

import java.util.List;

public interface RolePermissionService {

    void assignPermissionToRole(Long roleId, Long permissionId);

    void removePermissionFromRole(Long roleId, Long permissionId);

    List<PermissionResponse> getRolePermissions(Long roleId);
}
