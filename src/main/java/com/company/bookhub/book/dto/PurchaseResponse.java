package com.company.bookhub.book.dto;

import java.math.BigDecimal;

public record PurchaseResponse(
        Long bookId,
        String title,
        BigDecimal price,
        BigDecimal remainingBalance
) {
}
