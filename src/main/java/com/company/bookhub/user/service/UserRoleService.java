package com.company.bookhub.user.service;

import com.company.bookhub.role.dto.RoleResponse;

import java.util.List;

public interface UserRoleService {

    void assignRoleToUser(Long userId, Long roleId);

    void removeRoleFromUser(Long userId, Long roleId);

    List<RoleResponse> getUserRoles(Long userId);

}