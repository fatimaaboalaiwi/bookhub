package com.company.bookhub.accounting.dto;

import java.math.BigDecimal;

public record CheckPurchaseRequest(
        Long userId,
        BigDecimal amount
) {
}
