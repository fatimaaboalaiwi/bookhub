
package com.company.bookhub.ai.service;

import com.company.bookhub.book.entity.Book;
import com.company.bookhub.book.repository.BookRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class HybridBookSearchService {

    private static final int SEMANTIC_SEARCH_LIMIT = 4;
    private static final int KEYWORD_SEARCH_LIMIT = 10;
    private static final int RRF_CONSTANT = 60;

    private final VectorStore vectorStore;
    private final BookCatalogService bookCatalogService;
    private final BookRepository bookRepository;

    public HybridBookSearchService(
            VectorStore vectorStore,
            BookCatalogService bookCatalogService,
            BookRepository bookRepository
    ) {
        this.vectorStore = vectorStore;
        this.bookCatalogService = bookCatalogService;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<Book> searchBooks(String query, int resultLimit) {

        if (query == null || query.isBlank() || resultLimit <= 0) {
            return List.of();
        }

        Map<Long, Double> combinedScores = new HashMap<>();

        // 1. Semantic search using PGVector.
        List<Document> semanticResults = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(SEMANTIC_SEARCH_LIMIT)
                        .similarityThresholdAll()
                        .build()
        );

        for (int i = 0; i < semanticResults.size(); i++) {

            Long bookId = extractBookId(semanticResults.get(i));

            if (bookId != null) {
                double score = 1.0 / (RRF_CONSTANT + i + 1);
                combinedScores.merge(bookId, score, Double::sum);
            }
        }

        // 2. Keyword search using PostgreSQL.
        List<Book> keywordResults =
                bookCatalogService.searchBooksByKeywords(query)
                        .stream()
                        .limit(KEYWORD_SEARCH_LIMIT)
                        .toList();

        for (int i = 0; i < keywordResults.size(); i++) {

            Long bookId = keywordResults.get(i).getId();

            if (bookId != null) {
                double score = 1.0 / (RRF_CONSTANT + i + 1);
                combinedScores.merge(bookId, score, Double::sum);
            }
        }

        // 3. Combine and rank results.
        List<Long> rankedBookIds = combinedScores.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<Long, Double>comparingByValue(
                                Comparator.reverseOrder()
                        ).thenComparing(Map.Entry::getKey)
                )
                .limit(resultLimit)
                .map(Map.Entry::getKey)
                .toList();

        if (rankedBookIds.isEmpty()) {
            return List.of();
        }

        // 4. Reload the current database records.
        // This avoids relying on potentially outdated vector content.
        Map<Long, Book> currentBooks = bookRepository
                .findAllByIdInForRag(rankedBookIds)
                .stream()
                .collect(Collectors.toMap(
                        Book::getId,
                        Function.identity()
                ));

        // 5. Preserve the hybrid ranking order.
        // Books deleted from PostgreSQL are automatically excluded.
        return rankedBookIds.stream()
                .map(currentBooks::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private Long extractBookId(Document document) {

        if (document == null || document.getMetadata() == null) {
            return null;
        }

        Object value = document.getMetadata().get("bookId");

        if (value instanceof Number number) {
            return number.longValue();
        }

        if (value != null) {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }
}
