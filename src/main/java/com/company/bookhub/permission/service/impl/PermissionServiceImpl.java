package com.company.bookhub.permission.service.impl;

import com.company.bookhub.common.exception.DuplicateResourceException;
import com.company.bookhub.common.exception.ResourceNotFoundException;
import com.company.bookhub.permission.dto.PermissionRequest;
import com.company.bookhub.permission.dto.PermissionResponse;
import com.company.bookhub.permission.entity.Permission;
import com.company.bookhub.permission.repository.PermissionRepository;
import com.company.bookhub.permission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public PermissionResponse createPermission(PermissionRequest request) {

        if (permissionRepository.findByName(request.name()).isPresent()) {
            throw new DuplicateResourceException(
                    "permission.already_exists"
            );
        }

        Permission permission = new Permission(request.name());

        Permission savedPermission =
                permissionRepository.save(permission);

        return PermissionResponse.from(savedPermission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {

        return permissionRepository.findAll()
                .stream()
                .map(PermissionResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getPermissionById(Long id) {

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "permission.not_found"
                        )
                );

        return PermissionResponse.from(permission);
    }
}