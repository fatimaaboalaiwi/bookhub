package com.company.bookhub.Controller.access;

import com.company.bookhub.role.dto.RoleResponse;
import com.company.bookhub.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(
        name = "Roles",
        description = "APIs for retrieving roles and managing role permissions"
)
  public class RoleManagementController {

     private final RoleRepository roleRepository;

      @GetMapping
      @PreAuthorize("hasAuthority('GET_ROLE_PERMISSIONS')")
     public ResponseEntity<List<RoleResponse>> getAllRoles() {

        List<RoleResponse> roles = roleRepository.findAll()
                .stream()
                .map(RoleResponse::from)
                .toList();

        return ResponseEntity.ok(roles);
    }


}
