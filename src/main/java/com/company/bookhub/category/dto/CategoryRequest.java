package com.company.bookhub.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryRequest(

        @Schema(
                description = "Name of the category",
                example = "Programming",
                maxLength = 100
        )
        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must not exceed 100 characters")
        String name,

        @Schema(
                description = "Description of the category",
                example = "Books about programming and software development",
                maxLength = 255
        )
        @Size(max = 255, message = "Category description must not exceed 255 characters")
        String description

) {
}