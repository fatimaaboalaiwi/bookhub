package com.company.bookhub.book.service.impl;

import com.company.bookhub.author.entity.Author;
import com.company.bookhub.author.repository.AuthorRepository;
import com.company.bookhub.book.dto.BookRequest;
import com.company.bookhub.book.dto.BookResponse;
import com.company.bookhub.book.entity.Book;
import com.company.bookhub.book.mapper.BookMapper;
import com.company.bookhub.book.repository.BookRepository;
import com.company.bookhub.book.service.BookService;
import com.company.bookhub.book.specification.BookSpecification;
import com.company.bookhub.category.entity.Category;
import com.company.bookhub.category.repository.CategoryRepository;
import com.company.bookhub.common.dto.PageResponse;
import com.company.bookhub.common.exception.DuplicateResourceException;
import com.company.bookhub.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;


    // =========================
    // CREATE
    // =========================

    @Override
    public BookResponse create(BookRequest request) {

        // Check duplicate ISBN
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new DuplicateResourceException(
                    "Book with ISBN '" + request.isbn() + "' already exists"
            );
        }

        // Find author
        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Author with id " + request.authorId() + " not found"
                        )
                );

        // Find category
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category with id " + request.categoryId() + " not found"
                        )
                );

        // Create entity
        Book book = new Book();

        book.setTitle(request.title());
        book.setIsbn(request.isbn());
        book.setAuthor(author);
        book.setCategory(category);

        // Save
        Book savedBook = bookRepository.save(book);

        // Convert entity → DTO
        return bookMapper.toResponse(savedBook);
    }


    // =========================
    // GET BY ID
    // =========================

    @Override
    @Transactional(readOnly = true)
    public BookResponse getById(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book with id " + id + " not found"
                        )
                );

        return bookMapper.toResponse(book);
    }


    // =========================
    // GET ALL
    // PAGINATION + FILTERING
    // =========================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getAll(
            Long authorId,
            Long categoryId,
            String title,
            Pageable pageable) {

        /*
         * Build the specification.
         *
         * Each filter is optional.
         * If the value is null, that filter is ignored.
         */

        Specification<Book> specification =
                Specification.allOf(
                        BookSpecification.hasAuthorId(authorId),
                        BookSpecification.hasCategoryId(categoryId),
                        BookSpecification.titleContains(title)
                );

        /*
         * Ask Spring Data JPA to apply:
         *
         * 1. filters
         * 2. pagination
         * 3. sorting
         */

        Page<Book> bookPage =
                bookRepository.findAll(specification, pageable);

        /*
         * Convert:
         *
         * Page<Book>
         *
         * into:
         *
         * PageResponse<BookResponse>
         */

        return new PageResponse<>(
                bookPage
                        .getContent()
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


    // =========================
    // UPDATE
    // =========================

    @Override
    public BookResponse update(
            Long id,
            BookRequest request) {

        // Find existing book
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book with id " + id + " not found"
                        )
                );

        // Check duplicate ISBN
        if (!book.getIsbn().equals(request.isbn())
                && bookRepository.existsByIsbn(request.isbn())) {

            throw new DuplicateResourceException(
                    "Book with ISBN '" + request.isbn() + "' already exists"
            );
        }

        // Find author
        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Author with id " + request.authorId()
                                        + " not found"
                        )
                );

        // Find category
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category with id " + request.categoryId()
                                        + " not found"
                        )
                );

        // Update managed entity
        book.setTitle(request.title());
        book.setIsbn(request.isbn());
        book.setAuthor(author);
        book.setCategory(category);

        /*
         * Because this method is transactional,
         * Hibernate can detect these changes through
         * dirty checking.
         *
         * save() is not strictly necessary here.
         */

        return bookMapper.toResponse(book);
    }


    // =========================
    // DELETE
    // =========================

    @Override
    public void delete(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book with id " + id + " not found"
                        )
                );

        bookRepository.delete(book);
    }
}