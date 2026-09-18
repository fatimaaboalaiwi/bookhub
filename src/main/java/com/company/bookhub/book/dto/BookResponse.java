
package com.company.bookhub.book.dto;

import java.math.BigDecimal;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        Integer publishedYear,
        BigDecimal price,
        Long authorId,
        Long categoryId,
        String authorName,
        String categoryName
) {
}
