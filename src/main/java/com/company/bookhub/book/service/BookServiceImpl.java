
package com.company.bookhub.book.service;

import com.company.bookhub.author.entity.Author;
import com.company.bookhub.author.repository.AuthorRepository;
import com.company.bookhub.book.dto.BookRequest;
import com.company.bookhub.book.dto.BookResponse;
import com.company.bookhub.book.entity.Book;
import com.company.bookhub.book.mapper.BookMapper;
import com.company.bookhub.book.repository.BookRepository;
import com.company.bookhub.book.specification.BookSpecification;
import com.company.bookhub.category.entity.Category;
import com.company.bookhub.category.repository.CategoryRepository;
import com.company.bookhub.common.dto.PageResponse;
import com.company.bookhub.common.exception.DuplicateResourceException;
import com.company.bookhub.common.exception.ResourceNotFoundException;
import com.company.bookhub.ai.event.BookChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public BookResponse create(BookRequest request) {

        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new DuplicateResourceException("book.isbn_exists");
        }

        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("author.not_found")
                );

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("category.not_found")
                );

        Book book = new Book();

        book.setTitle(request.title());
        book.setIsbn(request.isbn());
        book.setPublishedYear(request.publishedYear());
        book.setPrice(request.price());
        book.setQty(request.qty() == null ? 1 : request.qty());
        book.setAuthor(author);
        book.setCategory(category);

        Book savedBook = bookRepository.save(book);

        eventPublisher.publishEvent(
                new BookChangedEvent(
                        savedBook.getId(),
                        BookChangedEvent.Operation.UPSERT
                )
        );

        return bookMapper.toResponse(savedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getById(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("book.not_found")
                );

        return bookMapper.toResponse(book);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getAll(
            Long authorId,
            Long categoryId,
            String title,
            Pageable pageable) {

        Specification<Book> specification =
                Specification.allOf(
                        BookSpecification.hasAuthorId(authorId),
                        BookSpecification.hasCategoryId(categoryId),
                        BookSpecification.titleContains(title)
                );

        Page<Book> bookPage =
                bookRepository.findAll(specification, pageable);

        return new PageResponse<>(
                bookPage.getContent()
                        .stream()
                        .map(bookMapper::toResponse)
                        .toList(),
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.isFirst(),
                bookPage.isLast()
        );
    }

    @Override
    public BookResponse update(Long id, BookRequest request) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("book.not_found")
                );

        if (!book.getIsbn().equals(request.isbn())
                && bookRepository.existsByIsbn(request.isbn())) {

            throw new DuplicateResourceException("book.isbn_exists");
        }

        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("author.not_found")
                );

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("category.not_found")
                );

        book.setTitle(request.title());
        book.setIsbn(request.isbn());
        book.setPublishedYear(request.publishedYear());
        book.setPrice(request.price());

        if (request.qty() != null) {
            book.setQty(request.qty());
        }

        book.setAuthor(author);
        book.setCategory(category);

        eventPublisher.publishEvent(
                new BookChangedEvent(
                        book.getId(),
                        BookChangedEvent.Operation.UPSERT
                )
        );

        return bookMapper.toResponse(book);
    }

    @Override
    public void delete(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("book.not_found")
                );

        bookRepository.delete(book);

        eventPublisher.publishEvent(
                new BookChangedEvent(
                        id,
                        BookChangedEvent.Operation.DELETE
                )
        );
    }
}
