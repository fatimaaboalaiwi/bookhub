package com.company.bookhub.book.service;

import com.company.bookhub.book.dto.BookRequest;
import com.company.bookhub.book.dto.BookResponse;
import com.company.bookhub.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookResponse create(BookRequest request);

    BookResponse getById(Long id);

    PageResponse<BookResponse> getAll(
            Long authorId,
            Long categoryId,
            String title,
            Pageable pageable
    );

    BookResponse update(Long id, BookRequest request);

    void delete(Long id);
}