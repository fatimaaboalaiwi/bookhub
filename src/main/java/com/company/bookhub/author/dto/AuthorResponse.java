
package com.company.bookhub.author.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}