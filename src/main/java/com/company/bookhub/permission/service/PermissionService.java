package com.company.bookhub.permission.service;

import com.company.bookhub.permission.dto.PermissionRequest;
import com.company.bookhub.permission.dto.PermissionResponse;

import java.util.List;

public interface PermissionService {

    PermissionResponse createPermission(PermissionRequest request);

    List<PermissionResponse> getAllPermissions();

    PermissionResponse getPermissionById(Long id);
}
