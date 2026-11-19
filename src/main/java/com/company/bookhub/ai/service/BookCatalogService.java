
package com.company.bookhub.ai.service;

import com.company.bookhub.ai.dto.BookResearchResult;
import com.company.bookhub.book.entity.Book;
import com.company.bookhub.book.repository.BookRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookCatalogService {

    private final BookRepository bookRepository;

    public BookCatalogService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public String getCompleteCatalogAnswer() {

        List<Book> books = bookRepository.findAllForCatalog();

        if (books.isEmpty()) {
            return "The BookHub catalog currently contains no books.";
        }

        StringBuilder answer = new StringBuilder();

        answer.append("Here are all ")
                .append(books.size())
                .append(" books in the BookHub catalog:\n\n");

        for (int i = 0; i < books.size(); i++) {

            Book book = books.get(i);

            answer.append(i + 1)
                    .append(". ")
                    .append(book.getTitle())
                    .append("\n")
                    .append("   Author: ")
                    .append(book.getAuthor() == null
                            ? "Not specified"
                            : book.getAuthor().getName())
                    .append("\n")
                    .append("   ISBN: ")
                    .append(book.getIsbn())
                    .append("\n")
                    .append("   Publication year: ")
                    .append(book.getPublishedYear() == null
                            ? "Not specified"
                            : book.getPublishedYear())
                    .append("\n")
                    .append("   Price: ")
                    .append(book.getPrice())
                    .append("\n")
                    .append("   Category: ")
                    .append(book.getCategory() == null
                            ? "Not specified"
                            : book.getCategory().getName())
                    .append("\n")
                    .append("   Quantity: ")
                    .append(getQuantity(book))
                    .append("\n")
                    .append("   Stock status: ")
                    .append(getStockStatus(book))
                    .append("\n\n");
        }

        return answer.toString().trim();
    }

    @Transactional(readOnly = true)
    public String getBookCountAnswer() {

        long count = bookRepository.findAllForCatalog().size();

        return "The BookHub catalog currently contains "
                + count
                + (count == 1 ? " book." : " books.");
    }

    @Transactional(readOnly = true)
    public String getInStockBookCountAnswer() {

        long count = bookRepository.findAllForCatalog()
                .stream()
                .filter(book -> book.getQty() != null && book.getQty() > 0)
                .count();

        return "There are currently " + count
                + (count == 1 ? " book" : " books")
                + " in stock in the BookHub catalog.";
    }

    @Transactional(readOnly = true)
    public String getOutOfStockBookCountAnswer() {

        long count = bookRepository.findAllForCatalog()
                .stream()
                .filter(book -> book.getQty() != null && book.getQty() == 0)
                .count();

        return "There are currently " + count
                + (count == 1 ? " book" : " books")
                + " out of stock in the BookHub catalog.";
    }

    @Transactional(readOnly = true)
    public String getExactBookAnswer(String title) {

        Optional<Book> result =
                bookRepository.findByTitleForAi(title);

        if (result.isEmpty()) {
            return "I couldn't find a book with the exact title '"
                    + title + "' in the BookHub catalog.";
        }

        Book book = result.get();

        return """
                Book details from the BookHub database:

                Title: %s
                Author: %s
                ISBN: %s
                Publication year: %s
                Price: %s
                Category: %s
                Quantity: %s
                Stock status: %s
                """.formatted(
                book.getTitle(),
                book.getAuthor() == null
                        ? "Not specified"
                        : book.getAuthor().getName(),
                book.getIsbn(),
                book.getPublishedYear() == null
                        ? "Not specified"
                        : book.getPublishedYear(),
                book.getPrice(),
                book.getCategory() == null
                        ? "Not specified"
                        : book.getCategory().getName(),
                getQuantity(book),
                getStockStatus(book)
        ).trim();
    }

    @Transactional(readOnly = true)
    public String getInStockBooksAnswer() {

        List<Book> inStockBooks = bookRepository.findAllForCatalog()
                .stream()
                .filter(book -> book.getQty() != null && book.getQty() > 0)
                .toList();

        if (inStockBooks.isEmpty()) {
            return "There are currently no books in stock in the BookHub catalog.";
        }

        StringBuilder answer = new StringBuilder();

        answer.append("The following ")
                .append(inStockBooks.size())
                .append(inStockBooks.size() == 1
                        ? " book is"
                        : " books are")
                .append(" currently in stock in BookHub:\n\n");

        for (int i = 0; i < inStockBooks.size(); i++) {

            Book book = inStockBooks.get(i);

            answer.append(i + 1)
                    .append(". ")
                    .append(book.getTitle())
                    .append("\n")
                    .append("   Quantity: ")
                    .append(getQuantity(book))
                    .append("\n")
                    .append("   Stock status: In stock\n")
                    .append("   Author: ")
                    .append(book.getAuthor() == null
                            ? "Not specified"
                            : book.getAuthor().getName())
                    .append("\n")
                    .append("   Category: ")
                    .append(book.getCategory() == null
                            ? "Not specified"
                            : book.getCategory().getName())
                    .append("\n\n");
        }

        return answer.toString().trim();
    }

    @Transactional(readOnly = true)
    public String getOutOfStockBooksAnswer() {

        List<Book> outOfStockBooks = bookRepository.findAllForCatalog()
                .stream()
                .filter(book -> book.getQty() != null && book.getQty() == 0)
                .toList();

        if (outOfStockBooks.isEmpty()) {
            return "There are currently no books with a verified quantity of zero "
                    + "in the BookHub catalog.";
        }

        StringBuilder answer = new StringBuilder();

        answer.append("The following ")
                .append(outOfStockBooks.size())
                .append(outOfStockBooks.size() == 1
                        ? " book is"
                        : " books are")
                .append(" currently out of stock in BookHub:\n\n");

        for (int i = 0; i < outOfStockBooks.size(); i++) {

            Book book = outOfStockBooks.get(i);

            answer.append(i + 1)
                    .append(". ")
                    .append(book.getTitle())
                    .append("\n")
                    .append("   Quantity: ")
                    .append(getQuantity(book))
                    .append("\n")
                    .append("   Stock status: Out of stock\n")
                    .append("   Author: ")
                    .append(book.getAuthor() == null
                            ? "Not specified"
                            : book.getAuthor().getName())
                    .append("\n")
                    .append("   Category: ")
                    .append(book.getCategory() == null
                            ? "Not specified"
                            : book.getCategory().getName())
                    .append("\n\n");
        }

        return answer.toString().trim();
    }

    @Transactional(readOnly = true)
    public String getBooksByCategoryAnswer(String categoryName) {

        List<Book> books =
                bookRepository.findAllByCategory_NameIgnoreCaseOrderByIdAsc(
                        categoryName
                );

        if (books.isEmpty()) {
            return "There are currently no books in the "
                    + categoryName + " category in BookHub.";
        }

        StringBuilder answer = new StringBuilder();

        answer.append("Here ")
                .append(books.size() == 1 ? "is " : "are ")
                .append(books.size())
                .append(books.size() == 1
                        ? " book "
                        : " books ")
                .append("in the ")
                .append(categoryName)
                .append(" category:\n\n");

        for (int i = 0; i < books.size(); i++) {

            Book book = books.get(i);

            answer.append(i + 1)
                    .append(". ")
                    .append(book.getTitle())
                    .append("\n")
                    .append("   Author: ")
                    .append(book.getAuthor() == null
                            ? "Not specified"
                            : book.getAuthor().getName())
                    .append("\n")
                    .append("   ISBN: ")
                    .append(book.getIsbn())
                    .append("\n")
                    .append("   Publication year: ")
                    .append(book.getPublishedYear() == null
                            ? "Not specified"
                            : book.getPublishedYear())
                    .append("\n")
                    .append("   Price: ")
                    .append(book.getPrice())
                    .append("\n")
                    .append("   Category: ")
                    .append(book.getCategory() == null
                            ? "Not specified"
                            : book.getCategory().getName())
                    .append("\n")
                    .append("   Quantity: ")
                    .append(getQuantity(book))
                    .append("\n")
                    .append("   Stock status: ")
                    .append(getStockStatus(book))
                    .append("\n\n");
        }

        answer.append(
                "Recommendation note: These books are listed because "
                        + "they belong to the requested category. "
                        + "BookHub does not currently provide book plots or "
                        + "summaries, so their story contents cannot be verified."
        );

        return answer.toString().trim();
    }

    @Transactional(readOnly = true)
    public String getExactBookFieldAnswer(String title, String requestedField) {

        Optional<Book> result = bookRepository.findByTitleForAi(title);

        if (result.isEmpty()) {
            return "I couldn't find the book '" + title
                    + "' in the BookHub catalog.";
        }

        Book book = result.get();

        String field = requestedField.toLowerCase(Locale.ROOT);

        return switch (field) {
            case "isbn" ->
                    "The ISBN of \"" + book.getTitle()
                            + "\" is " + book.getIsbn() + ".";

            case "author" ->
                    "The author of \"" + book.getTitle()
                            + "\" is "
                            + (book.getAuthor() == null
                            ? "not specified"
                            : book.getAuthor().getName()) + ".";

            case "category" ->
                    "The category of \"" + book.getTitle()
                            + "\" is "
                            + (book.getCategory() == null
                            ? "not specified"
                            : book.getCategory().getName()) + ".";

            case "price" ->
                    "The price of \"" + book.getTitle()
                            + "\" is "
                            + (book.getPrice() == null
                            ? "not specified"
                            : book.getPrice()) + ".";

            case "year" ->
                    "The publication year of \"" + book.getTitle()
                            + "\" is "
                            + (book.getPublishedYear() == null
                            ? "not specified"
                            : book.getPublishedYear()) + ".";

            case "quantity" ->
                    "The quantity of \"" + book.getTitle()
                            + "\" is " + getQuantity(book) + ".";

            case "stock" -> {
                if (book.getQty() == null) {
                    yield "The stock status of \"" + book.getTitle()
                            + "\" cannot be verified because its quantity "
                            + "is not specified in the database.";
                }

                yield "\"" + book.getTitle() + "\" has "
                        + getQuantity(book) + " "
                        + (book.getQty() == 1 ? "copy" : "copies")
                        + " and is "
                        + getStockStatus(book).toLowerCase(Locale.ROOT) + ".";
            }

            default ->
                    "I couldn't identify the requested book detail.";
        };
    }

    private String getQuantity(Book book) {
        return book.getQty() == null
                ? "Not specified"
                : String.valueOf(book.getQty());
    }

    private String getStockStatus(Book book) {

        if (book.getQty() == null) {
            return "Not specified";
        }

        return book.getQty() > 0
                ? "In stock"
                : "Out of stock";
    }

    @Transactional(readOnly = true)
    public Optional<String> findBookTitleInQuestion(String question) {

        String normalizedQuestion =
                question.toLowerCase(Locale.ROOT);

        return bookRepository.findAllForCatalog()
                .stream()
                .map(Book::getTitle)
                .filter(title -> normalizedQuestion.contains(
                        title.toLowerCase(Locale.ROOT)
                ))
                .findFirst();
    }

    @Transactional(readOnly = true)
    public List<Book> searchBooksByKeywords(String query) {

        if (query == null || query.isBlank()) {
            return List.of();
        }

        List<Book> books =
                bookRepository.searchByKeywordsForAi(query.trim());

        books.forEach(book -> {

            if (book.getAuthor() != null) {
                Hibernate.initialize(book.getAuthor());
            }

            if (book.getCategory() != null) {
                Hibernate.initialize(book.getCategory());
            }
        });

        return books;
    }

    @Transactional(readOnly = true)
    public Optional<String> findCategoryNameInQuestion(String question) {

        String normalizedQuestion =
                question.toLowerCase(Locale.ROOT);

        return bookRepository.findAllForCatalog()
                .stream()
                .filter(book -> book.getCategory() != null)
                .map(book -> book.getCategory().getName())
                .filter(category -> category != null)
                .distinct()
                .filter(category -> normalizedQuestion.contains(
                        category.toLowerCase(Locale.ROOT)
                ))
                .findFirst();
    }

    // Ordinary recommendations: preserve the existing behavior.
    @Transactional(readOnly = true)
    public List<Book> findRelevantBooksBySubject(String question) {

        return findRelevantBooksBySubject(question, false);
    }

    // Existing stock-aware method, still available to existing callers.
    @Transactional(readOnly = true)
    public List<Book> findRelevantBooksBySubject(
            String question,
            boolean onlyInStock
    ) {

        if (question == null || question.isBlank()) {
            return List.of();
        }

        List<Book> matchingBooks = searchBooksByKeywords(question);

        return findRelevantBooksBySubject(
                question,
                onlyInStock,
                matchingBooks
        );
    }

    // New method: validates hybrid-ranked candidates using the existing
    // category relevance and stock rules.
    @Transactional(readOnly = true)
    public List<Book> findRelevantBooksBySubject(
            String question,
            boolean onlyInStock,
            List<Book> candidates
    ) {

        if (question == null || question.isBlank()
                || candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        Set<String> questionTerms = extractMeaningfulTerms(question);

        if (questionTerms.isEmpty()) {
            return List.of();
        }

        return candidates.stream()
                .filter(book -> book != null && book.getId() != null)
                .filter(book -> !onlyInStock
                        || (book.getQty() != null && book.getQty() > 0))
                .filter(book -> book.getCategory() != null)
                .filter(book -> {

                    String categoryName =
                            book.getCategory().getName();

                    String categoryDescription =
                            book.getCategory().getDescription();

                    String categoryInformation =
                            (categoryName == null ? "" : categoryName)
                                    + " "
                                    + (categoryDescription == null
                                    ? ""
                                    : categoryDescription);

                    Set<String> categoryTerms =
                            extractMeaningfulTerms(categoryInformation);

                    return questionTerms.stream()
                            .anyMatch(categoryTerms::contains);
                })
                .collect(Collectors.toMap(
                        Book::getId,
                        book -> book,
                        (existing, duplicate) -> existing,
                        LinkedHashMap::new
                ))
                .values()
                .stream()
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<String> getBooksRelevantToSubjectAnswer(String question) {

        List<Book> relevantBooks =
                findRelevantBooksBySubject(question);

        if (relevantBooks.isEmpty()) {
            return Optional.empty();
        }

        StringBuilder answer = new StringBuilder();

        answer.append("Yes! I found ")
                .append(relevantBooks.size())
                .append(relevantBooks.size() == 1
                        ? " potentially relevant book "
                        : " potentially relevant books ")
                .append("in the BookHub catalog:\n\n");

        for (int i = 0; i < relevantBooks.size(); i++) {

            Book book = relevantBooks.get(i);

            answer.append(i + 1)
                    .append(". ")
                    .append(book.getTitle())
                    .append("\n")
                    .append("   Author: ")
                    .append(book.getAuthor() == null
                            ? "Not specified"
                            : book.getAuthor().getName())
                    .append("\n")
                    .append("   ISBN: ")
                    .append(book.getIsbn())
                    .append("\n")
                    .append("   Publication year: ")
                    .append(book.getPublishedYear() == null
                            ? "Not specified"
                            : book.getPublishedYear())
                    .append("\n")
                    .append("   Price: ")
                    .append(book.getPrice())
                    .append("\n")
                    .append("   Category: ")
                    .append(book.getCategory() == null
                            ? "Not specified"
                            : book.getCategory().getName())
                    .append("\n")
                    .append("   Category description: ")
                    .append(book.getCategory() == null
                            || book.getCategory().getDescription() == null
                            ? "Not available"
                            : book.getCategory().getDescription())
                    .append("\n")
                    .append("   Quantity: ")
                    .append(getQuantity(book))
                    .append("\n")
                    .append("   Stock status: ")
                    .append(getStockStatus(book))
                    .append("\n\n");
        }

        answer.append(
                "Relevance note: These books are identified as potentially "
                        + "relevant based on their category names and descriptions. "
                        + "This does not verify the specific contents of "
                        + "each individual book. BookHub does not currently "
                        + "provide detailed book summaries or contents."
        );

        return Optional.of(answer.toString().trim());
    }

    // Extracts meaningful words for category-description comparison.
    private Set<String> extractMeaningfulTerms(String text) {

        Set<String> ignoredWords = Set.of(
                "the", "a", "an", "and", "or", "but",
                "is", "are", "was", "were", "be", "been",
                "do", "does", "did", "have", "has",
                "i", "you", "we", "they", "my",
                "in", "on", "at", "to", "of", "for",
                "with", "from", "by", "about", "into",
                "any", "some", "there", "here",
                "book", "books", "available",
                "related", "relate", "recommend",
                "recommendation", "suggest", "find",
                "show", "list", "give", "me",
                "currently", "purchase", "buy", "stock"
        );

        Set<String> terms = new HashSet<>();

        for (String word : text.toLowerCase(Locale.ROOT)
                .split("[^a-z0-9]+")) {

            if (word.length() <= 2 || ignoredWords.contains(word)) {
                continue;
            }

            if (word.equals("fictional")) {
                word = "fiction";
            }

            terms.add(word);
        }

        return terms;
    }

    // Preserves the original method for existing callers.
    @Transactional(readOnly = true)
    public List<BookResearchResult> findRelevantBookDetailsBySubject(
            String question
    ) {

        return findRelevantBookDetailsBySubject(question, false);
    }

    // Existing method, still available to existing callers.
    @Transactional(readOnly = true)
    public List<BookResearchResult> findRelevantBookDetailsBySubject(
            String question,
            boolean onlyInStock
    ) {

        return findRelevantBookDetailsBySubject(
                question,
                onlyInStock,
                findRelevantBooksBySubject(question, onlyInStock)
        );
    }

    // New method: converts hybrid-ranked and validated candidates into
    // research results.
    @Transactional(readOnly = true)
    public List<BookResearchResult> findRelevantBookDetailsBySubject(
            String question,
            boolean onlyInStock,
            List<Book> candidates
    ) {

        return findRelevantBooksBySubject(
                question,
                onlyInStock,
                candidates
        )
                .stream()
                .map(book -> new BookResearchResult(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor() == null
                                ? null
                                : book.getAuthor().getName(),
                        book.getAuthor() == null
                                ? null
                                : book.getAuthor().getBiography(),
                        book.getIsbn(),
                        book.getPublishedYear(),
                        book.getPrice(),
                        book.getCategory() == null
                                ? null
                                : book.getCategory().getName(),
                        book.getCategory() == null
                                ? null
                                : book.getCategory().getDescription()
                ))
                .toList();
    }
}
