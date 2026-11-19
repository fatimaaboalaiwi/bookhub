
package com.company.bookhub.ai.service;

import com.company.bookhub.ai.dto.BookResearchResult;
import com.company.bookhub.book.entity.Book;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookResearchAgent {

    private static final int MAX_BOOKS_TO_RESEARCH = 3;
    private static final int HYBRID_SEARCH_LIMIT = 10;

    private final BookCatalogService bookCatalogService;
    private final HybridBookSearchService hybridBookSearchService;

    public BookResearchAgent(
            BookCatalogService bookCatalogService,
            HybridBookSearchService hybridBookSearchService
    ) {
        this.bookCatalogService = bookCatalogService;
        this.hybridBookSearchService = hybridBookSearchService;
    }

    public List<BookResearchResult> researchBySubject(String subject) {

        return researchBySubject(subject, false);
    }

    public List<BookResearchResult> researchBySubject(
            String subject,
            boolean onlyInStock
    ) {

        if (subject == null || subject.isBlank()) {
            return List.of();
        }

        // Retrieve candidates using semantic + keyword hybrid search.
        List<Book> hybridCandidates =
                hybridBookSearchService.searchBooks(
                        subject,
                        HYBRID_SEARCH_LIMIT
                );

        if (hybridCandidates.isEmpty()) {
            return List.of();
        }

        // Validate candidates using the existing category relevance
        // and stock rules, while preserving hybrid ranking order.
        return bookCatalogService
                .findRelevantBookDetailsBySubject(
                        subject,
                        onlyInStock,
                        hybridCandidates
                )
                .stream()
                .limit(MAX_BOOKS_TO_RESEARCH)
                .toList();
    }
}
