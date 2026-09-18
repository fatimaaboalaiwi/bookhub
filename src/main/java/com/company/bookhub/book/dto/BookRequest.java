
package com.company.bookhub.book.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record BookRequest(

        @NotBlank(message = "{validation.required}")
        @Size(max = 200, message = "{validation.max_length}")
        String title,

        @NotBlank(message = "{validation.required}")
        @Size(max = 20, message = "{validation.max_length}")
        String isbn,

        @NotNull(message = "{validation.required}")
        @Positive(message = "{validation.positive}")
        Integer publishedYear,

        @NotNull(message = "{validation.required}")
        @DecimalMin(value = "0.0", message = "{validation.min}")
        BigDecimal price,

        @NotNull(message = "{validation.required}")
        Long authorId,

        @NotNull(message = "{validation.required}")
        Long categoryId

) {
}
