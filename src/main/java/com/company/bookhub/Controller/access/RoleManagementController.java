
package com.company.bookhub.Controller.access;

import com.company.bookhub.role.dto.RoleRequest;
import com.company.bookhub.role.dto.RoleResponse;
import com.company.bookhub.role.entity.Role;
import com.company.bookhub.role.repository.RoleRepository;
import com.company.bookhub.role.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(
        name = "Roles",
        description = "APIs for viewing and managing roles"
)
public class RoleManagementController {

    private final RoleService roleService;
    private final RoleRepository roleRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('GET_ROLE_PERMISSIONS')")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<RoleResponse> createRole(
            @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleService.createRole(request));
    }

    @PutMapping("/{roleId}")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable Long roleId,
            @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.updateRole(roleId, request));
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Role not found"));

        if (role.isSystemRole()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "System roles cannot be deleted");
        }

        if (roleRepository.existsByUsers_Id(roleId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete a role assigned to users");
        }

        roleRepository.delete(role);

        return ResponseEntity.noContent().build();
    }
}
