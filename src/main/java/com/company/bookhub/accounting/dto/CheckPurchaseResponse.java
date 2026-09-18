package com.company.bookhub.accounting.dto;

import java.math.BigDecimal;

public record CheckPurchaseResponse(
        boolean sufficient,
        BigDecimal currentBalance,
        BigDecimal amount
) {
}
