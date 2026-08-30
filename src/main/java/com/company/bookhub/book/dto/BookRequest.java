package com.company.bookhub.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookRequest(

        @Schema(
                description = "Title of the book",
                example = "Effective Java",
                maxLength = 255
        )
        @NotBlank(message = "Book title is required")
        @Size(max = 255, message = "Book title must not exceed 255 characters")
        String title,

        @Schema(
                description = "International Standard Book Number",
                example = "978-0134685991",
                maxLength = 50
        )
        @NotBlank(message = "ISBN is required")
        @Size(max = 50, message = "ISBN must not exceed 50 characters")
        String isbn,

        @Schema(
                description = "ID of the author",
                example = "1"
        )
        @NotNull(message = "Author id is required")
        Long authorId,

        @Schema(
                description = "ID of the category",
                example = "1"
        )
        @NotNull(message = "Category id is required")
        Long categoryId

) {
}