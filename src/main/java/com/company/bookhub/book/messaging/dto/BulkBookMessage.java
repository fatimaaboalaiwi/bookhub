
package com.company.bookhub.book.messaging.dto;

import com.company.bookhub.book.dto.BookRequest;

import java.util.List;
import java.util.UUID;

public record BulkBookMessage(
        UUID batchId,
        List<BookRequest> books
) {
}

