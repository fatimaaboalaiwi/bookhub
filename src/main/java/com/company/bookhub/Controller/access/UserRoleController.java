package com.company.bookhub.Controller.access;

import com.company.bookhub.role.dto.RoleResponse;
import com.company.bookhub.user.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(
        name = "Access Management",
        description = "APIs for managing users, roles and permissions"
)
public class UserRoleController {

    private final UserRoleService userRoleService;

    @Operation(
            summary = "Assign role to user",
            description = "Assigns an existing role to a user"
    )
    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasAuthority('ASSIGN_USER_ROLE')")
    public ResponseEntity<Void> assignRoleToUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {

        userRoleService.assignRoleToUser(
                userId,
                roleId
        );

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Remove role from user",
            description = "Removes an assigned role from a user"
    )
    @DeleteMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasAuthority('REMOVE_USER_ROLE')")
    public ResponseEntity<Void> removeRoleFromUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {

        userRoleService.removeRoleFromUser(
                userId,
                roleId
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get user roles",
            description = "Returns all roles assigned to a user"
    )
    @GetMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('GET_USER_ROLES')")
    public ResponseEntity<List<RoleResponse>> getUserRoles(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                userRoleService.getUserRoles(userId)
        );
    }
}