package com.company.bookhub.role.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RolePermissionRequest(

        @NotEmpty(message = "{validation.required}")
        List<@NotNull(message = "{validation.required}") Long> permissionIds

) {
}
