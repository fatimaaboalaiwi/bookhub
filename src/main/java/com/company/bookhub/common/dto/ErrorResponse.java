package com.company.bookhub.common.dto;

import java.time.LocalDateTime;

// A Java Record is perfect for DTOs (Data Transfer Objects)
public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp
) {}