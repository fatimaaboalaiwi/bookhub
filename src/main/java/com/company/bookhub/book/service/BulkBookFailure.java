
package com.company.bookhub.book.service;

import java.util.List;

public record BulkBookFailure(
        int bookNumber,
        String isbn,
        List<String> errors
) {}

