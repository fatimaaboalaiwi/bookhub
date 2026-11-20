
package com.company.bookhub.book.service;

import com.company.bookhub.book.dto.BookRequest;
import com.company.bookhub.book.dto.BookResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BulkBookProcessingService {

    private final BookService bookService;

    public BulkBookProcessingService(BookService bookService) {
        this.bookService = bookService;
    }

    @Transactional
    public List<BookResponse> processBooks(List<BookRequest> requests) {

        List<BookResponse> createdBooks = new ArrayList<>();

        for (BookRequest request : requests) {
            BookResponse createdBook = bookService.create(request);
            createdBooks.add(createdBook);
        }

        return createdBooks;
    }
}
