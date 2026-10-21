package com.company.bookhub.role.service.impl;

import com.company.bookhub.role.dto.RoleRequest;
import com.company.bookhub.role.dto.RoleResponse;
import com.company.bookhub.role.entity.Role;
import com.company.bookhub.role.repository.RoleRepository;
import com.company.bookhub.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleResponse createRole(RoleRequest request) {
        String name = normalizeName(request.name());

        if (roleRepository.findByName(name).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A role with this name already exists"
            );
        }

        Role role = new Role();
        role.rename(name);

        Role savedRole = roleRepository.save(role);

        return RoleResponse.from(savedRole);
    }

    @Override
    public RoleResponse updateRole(Long roleId, RoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Role not found"
                ));

        if (role.isSystemRole()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "System roles cannot be renamed"
            );
        }

        String name = normalizeName(request.name());

        boolean nameUsedByAnotherRole = roleRepository.findByName(name)
                .filter(existingRole ->
                        !existingRole.getId().equals(roleId))
                .isPresent();

        if (nameUsedByAnotherRole) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A role with this name already exists"
            );
        }

        role.rename(name);

        Role savedRole = roleRepository.save(role);

        return RoleResponse.from(savedRole);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(RoleResponse::from)
                .toList();
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Role name is required"
            );
        }

        return name.trim().toUpperCase(Locale.ROOT);
    }
}