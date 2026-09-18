
package com.company.bookhub.author.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorResponse {

    @Schema(
            description = "Unique identifier of the author",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Full name of the author",
            example = "Joshua Bloch"
    )
    private String name;

    @Schema(
            description = "Biography of the author",
            example = "Author of Effective Java"
    )
    private String biography;

    @Schema(
            description = "Date and time the author was created",
            example = "2026-10-14T10:30:00+03:00"
    )
    private OffsetDateTime createdAt;
}
