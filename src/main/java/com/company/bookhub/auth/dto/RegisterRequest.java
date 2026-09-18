package com.company.bookhub.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "{validation.required}")
        @Size(
                max = 100,
                message = "{validation.max_length}"
        )
        String username,

        @NotBlank(message = "{validation.required}")
        @Size(
                min = 6,
                max = 100,
                message = "{validation.invalid}"
        )
        String password

) {
}