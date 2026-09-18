package com.company.bookhub.integration.numverify.dto;

import jakarta.validation.constraints.NotBlank;

public record PhoneValidationRequest(

        @NotBlank(message = "{validation.required}")
        String number

) {
}