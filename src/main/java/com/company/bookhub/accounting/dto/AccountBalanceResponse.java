package com.company.bookhub.accounting.dto;

import java.math.BigDecimal;

public record AccountBalanceResponse(
        Long userId,
        BigDecimal balance
) {
}
