
package com.company.bookhub.book.service;

import com.company.bookhub.ai.event.BookChangedEvent;
import com.company.bookhub.author.entity.Author;
import com.company.bookhub.author.repository.AuthorRepository;
import com.company.bookhub.book.dto.BookRequest;
import com.company.bookhub.book.dto.BookResponse;
import com.company.bookhub.book.entity.Book;
import com.company.bookhub.book.mapper.BookMapper;
import com.company.bookhub.book.messaging.repository.ProcessedBulkBatchRepository;
import com.company.bookhub.book.repository.BookRepository;
import com.company.bookhub.category.entity.Category;
import com.company.bookhub.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BulkBookProcessingService {

    private final BulkBookValidator bulkBookValidator;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ProcessedBulkBatchRepository processedBulkBatchRepository;

    @Transactional
    public BulkBookProcessingResult processBatch(
            UUID batchId,
            List<BookRequest> requests
    ) {
        if (batchId == null) {
            throw new IllegalArgumentException("Batch ID cannot be null");
        }

        int receivedCount = requests == null ? 0 : requests.size();

        int claimed = processedBulkBatchRepository.claimBatch(batchId);

        if (claimed == 0) {
            return new BulkBookProcessingResult(
                    receivedCount,
                    List.of(),
                    List.of(),
                    true
            );
        }

        BulkBookValidationResult validationResult =
                bulkBookValidator.validateBooks(requests);

        List<BookRequest> validRequests = validationResult.validRequests();

        List<BulkBookFailure> failures =
                new ArrayList<>(validationResult.failures());

        if (validRequests.isEmpty()) {
            processedBulkBatchRepository.updateBatchResult(
                    batchId,
                    receivedCount,
                    0,
                    failures.size()
            );

            return new BulkBookProcessingResult(
                    receivedCount,
                    List.of(),
                    List.copyOf(failures),
                    false
            );
        }

        List<Long> authorIds = validRequests.stream()
                .map(BookRequest::authorId)
                .distinct()
                .toList();

        Map<Long, Author> authorsById = authorRepository.findAllById(authorIds)
                .stream()
                .collect(Collectors.toMap(Author::getId, Function.identity()));

        List<Long> categoryIds = validRequests.stream()
                .map(BookRequest::categoryId)
                .distinct()
                .toList();

        Map<Long, Category> categoriesById = categoryRepository.findAllById(categoryIds)
                .stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));

        List<Book> booksToSave = new ArrayList<>();

        for (BookRequest request : validRequests) {
            Author author = authorsById.get(request.authorId());
            Category category = categoriesById.get(request.categoryId());

            if (author == null || category == null) {
                List<String> errors = new ArrayList<>();

                if (author == null) {
                    errors.add("Author with ID " + request.authorId() + " does not exist");
                }

                if (category == null) {
                    errors.add("Category with ID " + request.categoryId() + " does not exist");
                }

                failures.add(new BulkBookFailure(
                        requests.indexOf(request) + 1,
                        request.isbn(),
                        List.copyOf(errors)
                ));

                continue;
            }

            Book book = new Book();
            book.setTitle(request.title());
            book.setIsbn(request.isbn());
            book.setPublishedYear(request.publishedYear());
            book.setPrice(request.price());
            book.setQty(request.qty() == null ? 1 : request.qty());
            book.setAuthor(author);
            book.setCategory(category);

            booksToSave.add(book);
        }

        List<Book> savedBooks = bookRepository.saveAll(booksToSave);

        List<BookResponse> addedBooks = new ArrayList<>();

        for (Book savedBook : savedBooks) {
            addedBooks.add(bookMapper.toResponse(savedBook));

            eventPublisher.publishEvent(new BookChangedEvent(
                    savedBook.getId(),
                    BookChangedEvent.Operation.UPSERT
            ));
        }

        processedBulkBatchRepository.updateBatchResult(
                batchId,
                receivedCount,
                savedBooks.size(),
                failures.size()
        );

        return new BulkBookProcessingResult(
                receivedCount,
                List.copyOf(addedBooks),
                List.copyOf(failures),
                false
        );
    }
}
