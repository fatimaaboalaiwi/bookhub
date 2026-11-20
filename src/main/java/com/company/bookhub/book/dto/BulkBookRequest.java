
package com.company.bookhub.book.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record BulkBookRequest(

        @NotEmpty(message = "The book list must not be empty")
        @Size(max = 100, message = "A batch cannot contain more than 100 books")
        List<@Valid BookRequest> books

) {
}
