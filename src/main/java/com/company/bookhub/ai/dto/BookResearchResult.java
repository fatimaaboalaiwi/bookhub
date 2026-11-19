
package com.company.bookhub.ai.dto;

import java.math.BigDecimal;

public record BookResearchResult(
        Long bookId,
        String title,
        String authorName,
        String authorBiography,
        String isbn,
        Integer publishedYear,
        BigDecimal price,
        String categoryName,
        String categoryDescription
) {
}
