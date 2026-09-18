package com.company.bookhub.permission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PermissionRequest(

        @NotBlank(message = "{validation.required}")
        @Size(
                max = 100,
                message = "{validation.max_length}"
        )
        String name

) {
}