package com.company.bookhub.role.service.impl;

import com.company.bookhub.common.exception.DuplicateResourceException;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public void assignPermissionToRole(
            Long roleId,
            Long permissionId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "role.not_found"
                        )
                );

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "permission.not_found"
                        )
                );

        if (role.getPermissions().contains(permission)) {
            throw new DuplicateResourceException(
                    "permission.already_assigned"
            );
        }

        role.getPermissions().add(permission);

        roleRepository.save(role);
    }

    @Override
    public void removePermissionFromRole(
            Long roleId,
            Long permissionId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "role.not_found"
                        )
                );

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "permission.not_found"
                        )
                );

        if (!role.getPermissions().contains(permission)) {
            throw new ResourceNotFoundException(
                    "permission.not_assigned"
            );
        }

        role.getPermissions().remove(permission);

        roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getRolePermissions(
            Long roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "role.not_found"
                        )
                );

        return role.getPermissions()
                .stream()
                .map(PermissionResponse::from)
                .toList();
    }
}