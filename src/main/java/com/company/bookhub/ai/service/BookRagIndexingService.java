
package com.company.bookhub.ai.service;

import com.company.bookhub.book.entity.Book;
import com.company.bookhub.book.repository.BookRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BookRagIndexingService {

    private final BookRepository bookRepository;
    private final VectorStore vectorStore;

    public BookRagIndexingService(
            BookRepository bookRepository,
            VectorStore vectorStore
    ) {
        this.bookRepository = bookRepository;
        this.vectorStore = vectorStore;
    }

    // Index the complete catalog at application startup.
    public int indexBooks() {

        List<Book> books = bookRepository.findAllForRag();

        List<Document> documents = new ArrayList<>();

        for (Book book : books) {
            documents.add(buildBookDocument(book));
        }

        if (!documents.isEmpty()) {
            vectorStore.add(documents);
        }

        return documents.size();
    }

    // Index or refresh one book.
    public void indexBook(Long bookId) {

        Book book = bookRepository.findByIdForRag(bookId)
                .orElse(null);

        if (book == null) {
            deleteBook(bookId);
            return;
        }

        vectorStore.add(List.of(buildBookDocument(book)));
    }

    // Remove one book from the vector store.
    public void deleteBook(Long bookId) {

        String documentId = createDocumentId(bookId);

        vectorStore.delete(List.of(documentId));
    }

    // Build a document using the existing BookHub catalog format.
    private Document buildBookDocument(Book book) {

        String authorName = book.getAuthor() == null
                ? "Not available"
                : book.getAuthor().getName();

        String biography = book.getAuthor() == null
                || book.getAuthor().getBiography() == null
                ? "Not available"
                : book.getAuthor().getBiography();

        String categoryName = book.getCategory() == null
                ? "Not available"
                : book.getCategory().getName();

        String categoryDescription = book.getCategory() == null
                || book.getCategory().getDescription() == null
                ? "Not available"
                : book.getCategory().getDescription();

        String quantity = book.getQty() == null
                ? "Not specified"
                : String.valueOf(book.getQty());

        String stockStatus = book.getQty() != null && book.getQty() > 0
                ? "In stock"
                : "Out of stock";

        String content = """
                BOOKHUB CATALOG RECORD

                VERIFIED BOOK INFORMATION:
                Book Title: %s
                ISBN: %s
                Publication Year: %s
                Price: %s
                Category: %s
                Quantity: %s
                Stock Status: %s

                INVENTORY RULES:
                - Quantity is the exact number of copies recorded in BookHub.
                - A quantity greater than zero means In stock.
                - A quantity of zero means Out of stock.
                - Never invent or estimate book quantities.

                AUTHOR INFORMATION (NOT A BOOK DESCRIPTION):
                Author Name: %s
                Author Biography: %s

                CATEGORY INFORMATION (NOT A BOOK DESCRIPTION):
                Category Name: %s
                Category Description: %s

                IMPORTANT:
                - The category description describes the category,
                  not necessarily the contents of this individual book.
                - The author biography describes the author,
                  not the book's plot or contents.
                - No book plot, summary, or detailed contents
                  are provided in this record.
                """.formatted(
                book.getTitle(),
                book.getIsbn(),
                book.getPublishedYear() == null
                        ? "Not specified"
                        : book.getPublishedYear(),
                book.getPrice(),
                categoryName,
                quantity,
                stockStatus,
                authorName,
                biography,
                categoryName,
                categoryDescription
        );

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("bookId", book.getId());
        metadata.put("title", book.getTitle());
        metadata.put("author", authorName);
        metadata.put("category", categoryName);
        metadata.put("quantity", quantity);
        metadata.put("stockStatus", stockStatus);

        String documentId = createDocumentId(book.getId());

        return new Document(documentId, content, metadata);
    }

    // Generate the same stable ID for every indexing operation.
    private String createDocumentId(Long bookId) {

        return UUID.nameUUIDFromBytes(
                ("bookhub-book-" + bookId)
                        .getBytes(StandardCharsets.UTF_8)
        ).toString();
    }
}
