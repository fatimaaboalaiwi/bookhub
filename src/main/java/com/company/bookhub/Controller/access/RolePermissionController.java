package com.company.bookhub.Controller.access;

import com.company.bookhub.permission.dto.PermissionResponse;
import com.company.bookhub.role.dto.RolePermissionRequest;
import com.company.bookhub.role.service.RolePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(
        name = "Access Management",
        description = "APIs for managing users, roles and permissions"
)
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    @Operation(
            summary = "Manage role permissions",
            description = "Replaces the permissions currently assigned to a role"
    )
    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('ASSIGN_ROLE_PERMISSIONS')")
    public ResponseEntity<Void> manageRolePermissions(
            @PathVariable Long roleId,
            @Valid @RequestBody RolePermissionRequest request) {

        rolePermissionService.manageRolePermissions(
                roleId,
                request.permissionIds()
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get role permissions",
            description = "Returns all permissions currently assigned to a role"
    )
    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('GET_ROLE_PERMISSIONS')")
    public ResponseEntity<List<PermissionResponse>>
    getRolePermissions(
            @PathVariable Long roleId) {

        return ResponseEntity.ok(
                rolePermissionService.getRolePermissions(
                        roleId
                )
        );
    }
}