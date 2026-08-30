package com.company.bookhub.category.dto;
import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryResponse(

        @Schema(
                description = "Unique identifier of the category",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Name of the category",
                example = "Programming"
        )
        String name,

        @Schema(
                description = "Description of the category",
                example = "Books about programming and software development"
        )
        String description

) {
}