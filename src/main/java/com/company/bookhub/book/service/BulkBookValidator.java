
package com.company.bookhub.book.service;

import com.company.bookhub.author.repository.AuthorRepository;
import com.company.bookhub.book.dto.BookRequest;
import com.company.bookhub.book.repository.BookRepository;
import com.company.bookhub.category.repository.CategoryRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BulkBookValidator {

    private final Validator validator;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    public BulkBookValidator(
            Validator validator,
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            CategoryRepository categoryRepository
    ) {
        this.validator = validator;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
    }

    public BulkBookValidationResult validateBooks(
            List<BookRequest> requests
    ) {
        List<BookRequest> validRequests = new ArrayList<>();
        List<BulkBookFailure> failures = new ArrayList<>();

        if (requests == null) {
            return new BulkBookValidationResult(
                    validRequests,
                    List.of(new BulkBookFailure(
                            0,
                            null,
                            List.of("Book list cannot be null")
                    ))
            );
        }

        // Collect all non-null ISBNs.
        Set<String> incomingIsbns = requests.stream()
                .filter(request -> request != null)
                .map(BookRequest::isbn)
                .filter(isbn -> isbn != null && !isbn.isBlank())
                .collect(Collectors.toSet());

        // Retrieve existing ISBNs in one database query.
        Set<String> existingIsbns = bookRepository
                .findAllByIsbnIn(incomingIsbns)
                .stream()
                .map(book -> book.getIsbn())
                .collect(Collectors.toSet());

        // Collect author IDs and category IDs.
        Set<Long> authorIds = new HashSet<>();
        Set<Long> categoryIds = new HashSet<>();

        for (BookRequest request : requests) {
            if (request != null) {
                if (request.authorId() != null) {
                    authorIds.add(request.authorId());
                }

                if (request.categoryId() != null) {
                    categoryIds.add(request.categoryId());
                }
            }
        }

        // Load referenced authors and categories in batches.
        Set<Long> existingAuthorIds = authorRepository
                .findAllById(authorIds)
                .stream()
                .map(author -> author.getId())
                .collect(Collectors.toSet());

        Set<Long> existingCategoryIds = categoryRepository
                .findAllById(categoryIds)
                .stream()
                .map(category -> category.getId())
                .collect(Collectors.toSet());

        // Track ISBNs already encountered in this batch.
        Set<String> processedIsbns = new HashSet<>();

        // Validate each book independently.
        for (int i = 0; i < requests.size(); i++) {

            BookRequest request = requests.get(i);
            List<String> errors = new ArrayList<>();

            if (request == null) {
                errors.add("Book request cannot be null");
                failures.add(new BulkBookFailure(
                        i + 1,
                        null,
                        errors
                ));
                continue;
            }

            // Jakarta Bean Validation.
            Set<ConstraintViolation<BookRequest>> violations =
                    validator.validate(request);

            for (ConstraintViolation<BookRequest> violation : violations) {
                errors.add(
                        violation.getPropertyPath()
                                + ": "
                                + violation.getMessage()
                );
            }

            String isbn = request.isbn();

            if (isbn != null && !isbn.isBlank()) {

                // Check duplicates against PostgreSQL.
                if (existingIsbns.contains(isbn)) {
                    errors.add("ISBN already exists in the database");
                }

                // Check duplicates inside the incoming batch.
                if (!processedIsbns.add(isbn)) {
                    errors.add("Duplicate ISBN inside the batch");
                }
            }

            // Check author existence.
            if (request.authorId() != null
                    && !existingAuthorIds.contains(request.authorId())) {
                errors.add("Author with ID "
                        + request.authorId()
                        + " does not exist");
            }

            // Check category existence.
            if (request.categoryId() != null
                    && !existingCategoryIds.contains(request.categoryId())) {
                errors.add("Category with ID "
                        + request.categoryId()
                        + " does not exist");
            }

            if (errors.isEmpty()) {
                validRequests.add(request);
            } else {
                failures.add(new BulkBookFailure(
                        i + 1,
                        isbn,
                        List.copyOf(errors)
                ));
            }
        }

        return new BulkBookValidationResult(
                List.copyOf(validRequests),
                List.copyOf(failures)
        );
    }
}

