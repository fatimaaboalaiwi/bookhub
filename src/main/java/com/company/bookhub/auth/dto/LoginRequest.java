package com.company.bookhub.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "{validation.required}")
        String username,

        @NotBlank(message = "{validation.required}")
        String password

) {
}