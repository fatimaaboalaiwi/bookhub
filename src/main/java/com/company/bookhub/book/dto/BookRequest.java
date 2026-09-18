package com.company.bookhub.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookRequest(

        @NotBlank(message = "{validation.required}")
        @Size(
                max = 200,
                message = "{validation.max_length}"
        )
        String title,

        @NotBlank(message = "{validation.required}")
        @Size(
                max = 20,
                message = "{validation.max_length}"
        )
        String isbn,

        @NotNull(message = "{validation.required}")
        Long authorId,

        @NotNull(message = "{validation.required}")
        Long categoryId

) {
}