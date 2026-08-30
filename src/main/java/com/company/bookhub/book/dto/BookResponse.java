package com.company.bookhub.book.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

public record BookResponse(

        @Schema(
                description = "Unique identifier of the book",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Title of the book",
                example = "Effective Java"
        )
        String title,

        @Schema(
                description = "International Standard Book Number",
                example = "978-0134685991"
        )
        String isbn,

        @Schema(
                description = "ID of the author",
                example = "1"
        )
        Long authorId,

        @Schema(
                description = "ID of the category",
                example = "1"
        )
        Long categoryId
) {
}