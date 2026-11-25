
package com.company.bookhub.book.service;

import com.company.bookhub.book.dto.BookRequest;

import java.util.List;

public record BulkBookValidationResult(
        List<BookRequest> validRequests,
        List<BulkBookFailure> failures
) {}

