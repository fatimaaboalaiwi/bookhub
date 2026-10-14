package com.company.bookhub.role.service.impl;

import com.company.bookhub.common.exception.ResourceNotFoundException;
import com.company.bookhub.permission.dto.PermissionResponse;
import com.company.bookhub.permission.entity.Permission;
import com.company.bookhub.permission.repository.PermissionRepository;
import com.company.bookhub.role.entity.Role;
import com.company.bookhub.role.repository.RoleRepository;
import com.company.bookhub.role.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    //#TODO
//    delete assignPermissionToRole and removePermissionFromRole
//            create 1 funtion : manageRolePermissions: btekhod roleId, List of PermissionsId

    @Override
    public void manageRolePermissions(Long roleId, List<Long> permissionIds) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("role.not_found"));

        // 1. Keep system permissions already assigned to this role.
        Set<Permission> systemPermissions = role.getPermissions()
                .stream()
                .filter(Permission::isSystemPermission)
                .collect(Collectors.toSet());

        // 2. Remove duplicate IDs from the request.
        Set<Long> uniquePermissionIds = new HashSet<>(permissionIds);

        // 3. Load the permissions requested by the frontend.
        List<Permission> requestedPermissions =
                permissionRepository.findAllById(uniquePermissionIds);

        // 4. Ensure every requested permission exists.
        if (requestedPermissions.size() != uniquePermissionIds.size()) {
            throw new ResourceNotFoundException("permission.not_found");
        }

        // 5. Combine requested permissions with the preserved system permissions.
        Set<Permission> updatedPermissions =
                new HashSet<>(requestedPermissions);

        updatedPermissions.addAll(systemPermissions);

        // 6. Replace the role's permissions without losing its system permissions.
        role.getPermissions().clear();
        role.getPermissions().addAll(updatedPermissions);

        roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getRolePermissions(Long roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("role.not_found"));

        return role.getPermissions()
                .stream()
                .map(PermissionResponse::from)
                .toList();
    }
}