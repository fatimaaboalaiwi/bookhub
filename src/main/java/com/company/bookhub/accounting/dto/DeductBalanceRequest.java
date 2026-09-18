package com.company.bookhub.accounting.dto;

import java.math.BigDecimal;

public record DeductBalanceRequest(
        Long userId,
        BigDecimal amount
) {
}