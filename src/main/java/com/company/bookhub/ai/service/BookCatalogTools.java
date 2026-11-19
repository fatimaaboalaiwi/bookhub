
package com.company.bookhub.ai.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class BookCatalogTools {

    private final BookCatalogService bookCatalogService;

    public BookCatalogTools(BookCatalogService bookCatalogService) {
        this.bookCatalogService = bookCatalogService;
    }

    @Tool(
            name = "get_book_count",
            description = "Get the exact number of books currently stored in the BookHub database."
    )
    public String getBookCount() {
        return bookCatalogService.getBookCountAnswer();
    }

    @Tool(
            name = "get_complete_book_catalog",
            description = "Retrieve all books currently available in the BookHub database, including their titles, authors, ISBNs, publication years, prices, and categories."
    )
    public String getCompleteCatalog() {
        return bookCatalogService.getCompleteCatalogAnswer();
    }

    @Tool(
            name = "get_book_details",
            description = "Retrieve verified database details for a specific book using its exact title."
    )
    public String getBookDetails(
            @ToolParam(description = "The exact title of the book to look up.")
            String title
    ) {
        return bookCatalogService.getExactBookAnswer(title);
    }

    @Tool(
            name = "search_books_by_subject",
            description = "Find potentially relevant books by comparing the requested subject with book category names and descriptions. Use this for subject-based recommendations. The result does not verify individual book contents."
    )
    public String searchBooksBySubject(
            @ToolParam(description = "The subject or topic the user is interested in.")
            String subject
    ) {
        return bookCatalogService
                .getBooksRelevantToSubjectAnswer(subject)
                .orElse(
                        "No potentially relevant books were found for this subject in the BookHub catalog."
                );
    }
}
