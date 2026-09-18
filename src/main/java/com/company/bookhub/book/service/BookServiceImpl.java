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
                    "book.isbn_exists"
            );
        }

        // Find author
        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "author.not_found"
                        )
                );

        // Find category
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "category.not_found"
                        )
                );

        // Create entity
        Book book = new Book();

        book.setTitle(request.title());
        book.setIsbn(request.isbn());
        book.setPublishedYear(request.publishedYear());
        book.setPrice(request.price());
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
                                "book.not_found"
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

        Specification<Book> specification =
                Specification.allOf(
                        BookSpecification.hasAuthorId(authorId),
                        BookSpecification.hasCategoryId(categoryId),
                        BookSpecification.titleContains(title)
                );

        Page<Book> bookPage =
                bookRepository.findAll(specification, pageable);

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
                                "book.not_found"
                        )
                );

        // Check duplicate ISBN
        if (!book.getIsbn().equals(request.isbn())
                && bookRepository.existsByIsbn(request.isbn())) {

            throw new DuplicateResourceException(
                    "book.isbn_exists"
            );
        }

        // Find author
        Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "author.not_found"
                        )
                );

        // Find category
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "category.not_found"
                        )
                );

        // Update managed entity
        book.setTitle(request.title());
        book.setIsbn(request.isbn());
        book.setPublishedYear(request.publishedYear());
        book.setPrice(request.price());
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
                                "book.not_found"
                        )
                );

        bookRepository.delete(book);
    }
}