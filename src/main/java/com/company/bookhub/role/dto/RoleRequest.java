package com.company.bookhub.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoleRequest(

        @NotBlank(message = "{validation.required}")
        @Size(max = 50, message = "{validation.size}")
        String name

) {
}