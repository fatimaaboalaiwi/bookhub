package com.company.bookhub.permission.controller;

import com.company.bookhub.permission.dto.PermissionRequest;
import com.company.bookhub.permission.dto.PermissionResponse;
import com.company.bookhub.permission.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PermissionResponse> createPermission(
            @Valid @RequestBody PermissionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(permissionService.createPermission(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {

        return ResponseEntity.ok(
                permissionService.getAllPermissions()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PermissionResponse> getPermissionById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                permissionService.getPermissionById(id)
        );
    }
}