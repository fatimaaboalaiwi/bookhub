package com.company.bookhub.author.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthorRequest {

    @Schema(
            description = "Full name of the author",
            example = "Joshua Bloch",
            maxLength = 150
    )
    @Size(
            max = 150,
            message = "{validation.max_length}"
    )
    @NotBlank(message = "{validation.required}")
    private String name;
}