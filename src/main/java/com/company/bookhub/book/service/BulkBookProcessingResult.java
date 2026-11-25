
package com.company.bookhub.book.service;

import com.company.bookhub.book.dto.BookResponse;

import java.util.List;

public record BulkBookProcessingResult(
        int receivedCount,
        List<BookResponse> addedBooks,
        List<BulkBookFailure> failures,
        boolean alreadyProcessed
) {
    public int addedCount() {
        return addedBooks.size();
    }

    public int failedCount() {
        return failures.size();
    }
}
