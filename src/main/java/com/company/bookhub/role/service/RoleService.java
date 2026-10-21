package com.company.bookhub.role.service;

import com.company.bookhub.role.dto.RoleRequest;
import com.company.bookhub.role.dto.RoleResponse;

import java.util.List;

public interface RoleService {

    RoleResponse createRole(RoleRequest request);

    RoleResponse updateRole(Long roleId, RoleRequest request);

    List<RoleResponse> getAllRoles();
}