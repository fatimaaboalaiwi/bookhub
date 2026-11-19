
package com.company.bookhub.ai.service;

import com.company.bookhub.ai.dto.AiChatResponse;
import com.company.bookhub.ai.dto.BookResearchResult;
import com.company.bookhub.book.entity.Book;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AiChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final HybridBookSearchService hybridBookSearchService;
    private final BookCatalogService bookCatalogService;
    private final BookCatalogTools bookCatalogTools;
    private final BookResearchAgent bookResearchAgent;

    public AiChatService(
            ChatClient.Builder chatClientBuilder,
            ChatMemory chatMemory,
            HybridBookSearchService hybridBookSearchService,
            BookCatalogService bookCatalogService,
            BookCatalogTools bookCatalogTools,
            BookResearchAgent bookResearchAgent
    ) {

        this.hybridBookSearchService = hybridBookSearchService;
        this.bookCatalogService = bookCatalogService;
        this.bookCatalogTools = bookCatalogTools;
        this.bookResearchAgent = bookResearchAgent;
        this.chatMemory = chatMemory;

        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        You are BookHub AI Assistant.

                        Your responsibilities include:
                        - Helping users discover books available in BookHub.
                        - Answering questions about authors and literature.
                        - Providing reading recommendations.
                        - Answering general educational questions.

                        Communication style:
                        - Be friendly, clear, and concise.
                        - Organize answers using short lists when useful.
                        - Ask for clarification when a question is ambiguous.

                        CONVERSATION MEMORY AND IDENTITY:

                        - Treat information the user shares about themselves as information
                          about the user, not about yourself.
                        - If the user says "My name is Alex", remember that Alex is the user's
                          name, not the assistant's name.
                        - When asked "What is my name?", answer using the user's previously
                          provided name when it is available in conversation memory.
                        - Never confuse the user's identity, preferences, or personal
                          information with your own identity.
                        - You are BookHub AI Assistant. Do not claim to be the user.
                        - If personal information is not available in conversation memory,
                          say that you do not know rather than guessing.

                        STRICT RULES FOR BOOKHUB DATABASE INFORMATION:

                        1. Use supplied BookHub context as the source of
                           truth for actual books, authors, and categories.

                        2. Never invent book titles, ISBNs, author names,
                           biographies, publication years, prices,
                           quantities, stock statuses, categories, or descriptions.

                        3. Only state an author biography if it is explicitly
                           available in the supplied context.

                        4. CATEGORY-BASED RELEVANCE:
                           - A category name and its description are valid
                             evidence for identifying books that may be
                             relevant to a user's requested subject.
                           - If a category description directly matches
                             the requested subject, you may recommend
                             books belonging to that category.
                           - Do not reject a book merely because its title
                             does not contain the requested keywords.
                           - Clearly explain when a recommendation is
                             based on category-level relevance.

                        5. BOOK CONTENT ACCURACY:
                           - A category description describes the category,
                             not necessarily the contents of every book
                             assigned to that category.
                           - Never claim that a specific book covers
                             a particular programming language, technology,
                             topic, plot, or subject unless the supplied
                             context explicitly confirms it.
                           - Do not invent a book plot, summary, or contents.
                           - When detailed book contents are unavailable,
                             clearly say that they cannot be verified.

                        6. If supplied context does not contain enough
                           information, clearly explain this limitation.

                        7. Do not claim that a partial search represents
                           the complete BookHub catalog.

                        8. Recommend only books present in supplied
                           BookHub context.

                        9. Quantity and stock status must come from supplied
                           database records. A book with quantity zero is
                           out of stock. Do not describe it as available for purchase.

                        GENERAL EDUCATIONAL QUESTIONS:

                        - You may answer using your general knowledge.
                        - Distinguish general explanations from facts
                          about BookHub's actual inventory.
                        - Do not make unsupported assumptions.

                        IMPORTANT:
                        - Never claim to have added, updated, deleted,
                          or purchased anything in BookHub.
                        - Never claim to have accessed information that
                          was not supplied in the context.
                        """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    public AiChatResponse chat(
            String message,
            String conversationId,
            String username
    ) {

        String scopedConversationId = UUID.nameUUIDFromBytes(
                (username + ":" + conversationId)
                        .getBytes(StandardCharsets.UTF_8)
        ).toString();

        // 0. Answer identity questions directly from conversation memory.
        if (isIdentityQuestion(message)) {

            Optional<String> userName =
                    findUserNameInConversation(scopedConversationId);

            String answer = userName
                    .map(name -> "Your name is " + name + ".")
                    .orElse("You haven't told me your name in this conversation.");

            chatMemory.add(
                    scopedConversationId,
                    List.of(
                            new UserMessage(message),
                            new AssistantMessage(answer)
                    )
            );

            return new AiChatResponse(answer);
        }

        // 1. Identify an exact book title in the current question.
        Optional<String> bookTitle =
                bookCatalogService.findBookTitleInQuestion(message);

        // 2. Handle questions about a specifically named book first.
        if (bookTitle.isPresent()
                && (isExactBookFactQuestion(message)
                || isBookDetailsQuestion(message))) {

            String answer;

            if (isBookDetailsQuestion(message)) {

                answer = bookCatalogService.getExactBookAnswer(
                        bookTitle.get()
                );

            } else {

                Optional<String> requestedField =
                        findRequestedBookField(message);

                answer = requestedField
                        .map(field -> bookCatalogService.getExactBookFieldAnswer(
                                bookTitle.get(),
                                field
                        ))
                        .orElseGet(() -> bookCatalogService.getExactBookAnswer(
                                bookTitle.get()
                        ));
            }

            chatMemory.add(
                    scopedConversationId,
                    List.of(
                            new UserMessage(message),
                            new AssistantMessage(answer)
                    )
            );

            return new AiChatResponse(answer);
        }

        // 3. Handle stock-count questions before general catalog questions.
        if (isStockCountQuestion(message)) {

            String answer = isOutOfStockQuestion(message)
                    ? bookCatalogService.getOutOfStockBookCountAnswer()
                    : bookCatalogService.getInStockBookCountAnswer();

            chatMemory.add(
                    scopedConversationId,
                    List.of(
                            new UserMessage(message),
                            new AssistantMessage(answer)
                    )
            );

            return new AiChatResponse(answer);
        }

        // 4. Handle stock-list questions before general catalog questions.
        if (isStockListQuestion(message)) {

            String answer;

            if (isOutOfStockQuestion(message)) {
                answer = bookCatalogService.getOutOfStockBooksAnswer();
            } else {
                answer = bookCatalogService.getInStockBooksAnswer();
            }

            chatMemory.add(
                    scopedConversationId,
                    List.of(
                            new UserMessage(message),
                            new AssistantMessage(answer)
                    )
            );

            return new AiChatResponse(answer);
        }

        // 5. Get the complete catalog directly from PostgreSQL.
        if (isCatalogQuestion(message)) {

            String answer =
                    bookCatalogService.getCompleteCatalogAnswer();

            chatMemory.add(
                    scopedConversationId,
                    List.of(
                            new UserMessage(message),
                            new AssistantMessage(answer)
                    )
            );

            return new AiChatResponse(answer);
        }

        // 6. Get the exact book count directly from PostgreSQL.
        if (isCountQuestion(message)) {

            String answer = bookCatalogService.getBookCountAnswer();

            chatMemory.add(
                    scopedConversationId,
                    List.of(
                            new UserMessage(message),
                            new AssistantMessage(answer)
                    )
            );

            return new AiChatResponse(answer);
        }

        // 7. Handle exact book facts when the book was mentioned earlier.
        if (isExactBookFactQuestion(message)
                || isBookDetailsQuestion(message)) {

            Optional<String> requestedField =
                    findRequestedBookField(message);

            Optional<String> previousBookTitle =
                    findBookTitleInConversation(scopedConversationId);

            if (previousBookTitle.isPresent()) {

                String answer;

                if (isBookDetailsQuestion(message)) {

                    answer = bookCatalogService.getExactBookAnswer(
                            previousBookTitle.get()
                    );

                } else if (requestedField.isPresent()) {

                    answer = bookCatalogService.getExactBookFieldAnswer(
                            previousBookTitle.get(),
                            requestedField.get()
                    );

                } else {

                    answer = bookCatalogService.getExactBookAnswer(
                            previousBookTitle.get()
                    );
                }

                chatMemory.add(
                        scopedConversationId,
                        List.of(
                                new UserMessage(message),
                                new AssistantMessage(answer)
                        )
                );

                return new AiChatResponse(answer);
            }

            String answer = "Which book are you asking about?";

            chatMemory.add(
                    scopedConversationId,
                    List.of(
                            new UserMessage(message),
                            new AssistantMessage(answer)
                    )
            );

            return new AiChatResponse(answer);
        }

        // 8. Use the research agent for subject-based book searches.
        if (isSubjectResearchQuestion(message)) {
            return answerWithResearchAgent(
                    message,
                    scopedConversationId
            );
        }

        // 9. Handle ordinary conversation without injecting BookHub records.
        if (isGeneralConversationQuestion(message)) {

            String response = chatClient
                    .prompt()
                    .user(message)
                    .advisors(advisor -> advisor.param(
                            ChatMemory.CONVERSATION_ID,
                            scopedConversationId
                    ))
                    .call()
                    .content();

            return new AiChatResponse(response);
        }

        // 10. Shared hybrid retrieval: semantic search + keyword search + RRF.
        List<Book> hybridBooks =
                hybridBookSearchService.searchBooks(message, 4);

        String context = hybridBooks.stream()
                .map(this::buildBookContext)
                .collect(Collectors.joining("\n\n---\n\n"));

        if (context.isBlank()) {
            context = "No relevant BookHub records were retrieved.";
        }

        // 11. Generate the final answer using the current database records.
        String userPrompt = """
                Answer the user's question using the system instructions.

                BOOKHUB DATABASE CONTEXT:
                The following records were retrieved using a combination
                of semantic vector search and PostgreSQL keyword search.
                Their details were reloaded from the current PostgreSQL records.

                IMPORTANT INTERPRETATION GUIDANCE:

                - Examine category names and category descriptions when
                  deciding whether a book may be relevant to the user's
                  requested subject.
                - A category description can establish category-level
                  relevance even when the book title does not contain
                  the requested keywords.
                - Do not reject a book just because its title is different
                  from the requested subject.
                - Explain the basis of the match accurately.
                - Do not present category-level relevance as proof of
                  the individual book's actual contents.
                - If the record does not provide a book summary or
                  detailed contents, explicitly state that limitation.
                - Recommend only books included in the retrieved records.
                - If quantity and stock information are present, report
                  those exact values. Do not guess missing values.

                %s

                USER QUESTION:

                %s

                Remember:
                - Use only supplied context for specific BookHub facts.
                - Do not make unsupported assumptions.
                - If the context is insufficient, say so.
                - General educational questions may be answered
                  using general knowledge.
                """.formatted(context, message);

        String response = chatClient
                .prompt()
                .user(userPrompt)
                .tools(bookCatalogTools)
                .advisors(advisor -> advisor.param(
                        ChatMemory.CONVERSATION_ID,
                        scopedConversationId
                ))
                .call()
                .content();

        return new AiChatResponse(response);
    }

    private String buildBookContext(Book book) {

        String authorName = book.getAuthor() == null
                ? "Not specified"
                : book.getAuthor().getName();

        String biography = book.getAuthor() == null
                ? null
                : book.getAuthor().getBiography();

        String categoryName = book.getCategory() == null
                ? "Not specified"
                : book.getCategory().getName();

        String categoryDescription = book.getCategory() == null
                ? null
                : book.getCategory().getDescription();

        String quantity = book.getQty() == null
                ? "Not specified"
                : String.valueOf(book.getQty());

        String stockStatus = book.getQty() == null
                ? "Not specified"
                : book.getQty() > 0
                ? "In stock"
                : "Out of stock";

        return """
                BOOKHUB CATALOG RECORD

                VERIFIED BOOK INFORMATION:
                Book Title: %s
                ISBN: %s
                Publication Year: %s
                Price: %s
                Category: %s
                Quantity: %s
                Stock Status: %s

                AUTHOR INFORMATION (NOT A BOOK DESCRIPTION):
                Author Name: %s
                Author Biography: %s

                CATEGORY INFORMATION:
                Category Name: %s
                Category Description: %s

                IMPORTANT:
                - Quantity and stock status are database inventory facts.
                - A quantity greater than zero means the book is in stock.
                - A quantity of zero means the book is out of stock.
                - The category description describes the category.
                - Category-level relevance does not prove the actual
                  contents of this individual book.
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
                biography == null ? "Not available" : biography,
                categoryName,
                categoryDescription == null
                        ? "Not available"
                        : categoryDescription
        );
    }

    private Optional<String> findBookTitleInConversation(
            String conversationId
    ) {

        List<Message> messages = chatMemory.get(conversationId);

        for (int i = messages.size() - 1; i >= 0; i--) {

            Optional<String> title =
                    bookCatalogService.findBookTitleInQuestion(
                            messages.get(i).getText()
                    );

            if (title.isPresent()) {
                return title;
            }
        }

        return Optional.empty();
    }

    private Optional<String> findRequestedBookField(String message) {

        String normalized = normalize(message);

        if (normalized.contains("isbn")) {
            return Optional.of("isbn");
        }

        if (normalized.contains("author")
                || normalized.contains("writer")
                || normalized.contains("who wrote")) {
            return Optional.of("author");
        }

        if (normalized.contains("category")
                || normalized.contains("genre")) {
            return Optional.of("category");
        }

        if (normalized.contains("price")
                || normalized.contains("cost")
                || normalized.contains("how much")) {
            return Optional.of("price");
        }

        if (normalized.contains("publication year")
                || normalized.contains("published year")
                || normalized.contains("what year")
                || normalized.contains("when was it published")) {
            return Optional.of("year");
        }

        if (normalized.contains("quantity")
                || normalized.contains("quantities")
                || normalized.contains("how many copies")
                || normalized.contains("number of copies")
                || normalized.contains("copies left")) {
            return Optional.of("quantity");
        }

        if (normalized.contains("stock")
                || normalized.contains("in stock")
                || normalized.contains("out of stock")
                || normalized.contains("availability")) {
            return Optional.of("stock");
        }

        return Optional.empty();
    }

    private boolean isStockCountQuestion(String message) {

        String normalized = normalize(message);

        boolean asksForCount =
                normalized.matches(
                        ".*\\b(how many|count|number of)\\b.*"
                                + "\\b(books|book)\\b.*"
                );

        boolean asksAboutStock =
                normalized.contains("in stock")
                        || normalized.contains("out of stock")
                        || normalized.contains("not in stock")
                        || normalized.contains("no stock");

        return asksForCount && asksAboutStock;
    }

    private boolean isStockListQuestion(String message) {

        String normalized = normalize(message);

        boolean asksAboutStock =
                normalized.contains("in stock")
                        || normalized.contains("out of stock")
                        || normalized.contains("stock availability")
                        || normalized.contains("stock status")
                        || normalized.contains("books have stock")
                        || normalized.contains("books with stock")
                        || normalized.contains("available for purchase");

        boolean asksForBookList =
                normalized.matches(
                        ".*\\b(which|what|list|show|display|all)\\b.*"
                )
                        && normalized.matches(
                        ".*\\b(books|book|titles)\\b.*"
                );

        return asksAboutStock && asksForBookList;
    }

    private boolean isOutOfStockQuestion(String message) {

        String normalized = normalize(message);

        return normalized.contains("out of stock")
                || normalized.contains("not in stock")
                || normalized.contains("no stock");
    }

    private boolean isSubjectResearchQuestion(String message) {

        String normalized = normalize(message);

        boolean mentionsBooks = normalized.matches(
                ".*\\b(books?|titles?)\\b.*"
        );

        boolean hasSubjectIntent = normalized.matches(
                ".*\\b(about|on|related to|regarding|concerning|covering|for|in|recommend|recommendation|suggest|find|looking for)\\b.*"
        );

        return mentionsBooks
                && hasSubjectIntent
                && !isExactBookFactQuestion(message)
                && !isStockListQuestion(message)
                && !isStockCountQuestion(message);
    }

    private boolean isGeneralConversationQuestion(String message) {

        String normalized = normalize(message);

        return !normalized.matches(
                ".*\\b(book|books|title|titles|author|authors|isbn|catalog|inventory|category|categories|price|published|publication|quantity|quantities|stock|copies|bookhub)\\b.*"
        );
    }

    private AiChatResponse answerWithResearchAgent(
            String message,
            String scopedConversationId
    ) {

        boolean onlyInStock =
                isAvailabilityRecommendationQuestion(message);

        List<BookResearchResult> results =
                bookResearchAgent.researchBySubject(
                        message,
                        onlyInStock
                );

        if (results.isEmpty()) {

            String answer = onlyInStock
                    ? "There are currently no matching books available "
                    + "for purchase in the BookHub catalog."
                    : "I couldn't find any books matching that subject "
                    + "in the current BookHub catalog.";

            return new AiChatResponse(answer);
        }

        StringBuilder answer = new StringBuilder();

        answer.append(
                "I found the following potentially relevant books "
                        + "in the BookHub catalog:\n\n"
        );

        for (int i = 0; i < results.size(); i++) {

            BookResearchResult book = results.get(i);

            answer.append(i + 1)
                    .append(". **")
                    .append(book.title() == null
                            ? "Title not available"
                            : book.title())
                    .append("**\n");

            answer.append("   - Author: ")
                    .append(book.authorName() == null
                            ? "Not available"
                            : book.authorName())
                    .append("\n");

            answer.append("   - ISBN: ")
                    .append(book.isbn() == null
                            ? "Not available"
                            : book.isbn())
                    .append("\n");

            answer.append("   - Publication year: ")
                    .append(book.publishedYear() == null
                            ? "Not available"
                            : book.publishedYear())
                    .append("\n");

            answer.append("   - Price: ")
                    .append(book.price() == null
                            ? "Not available"
                            : book.price())
                    .append("\n");

            answer.append("   - Category: ")
                    .append(book.categoryName() == null
                            ? "Not available"
                            : book.categoryName())
                    .append("\n");

            answer.append("   - Category description: ")
                    .append(book.categoryDescription() == null
                            ? "Not available"
                            : book.categoryDescription())
                    .append("\n");

            if (onlyInStock) {
                answer.append("   - Availability: In stock\n");
            }

            answer.append("\n");
        }

        answer.append(
                "Relevance note: These books were identified using "
                        + "their category names and descriptions. This establishes "
                        + "potential category-level relevance, but does not verify "
                        + "the actual contents of each individual book. "
                        + "BookHub does not currently provide verified book summaries."
        );

        return new AiChatResponse(answer.toString());
    }

    private boolean isAvailabilityRecommendationQuestion(
            String message
    ) {

        String normalized = normalize(message);

        return normalized.contains("available for purchase")
                || normalized.contains("available to buy")
                || normalized.contains("currently available")
                || normalized.contains("in stock")
                || normalized.contains("can buy")
                || normalized.contains("for sale");
    }

    private boolean isExactBookFactQuestion(String message) {

        String normalized = normalize(message);

        return normalized.matches(
                ".*\\b(author|wrote|written by|isbn|price|cost|year|published|publication|category|details|quantity|quantities|stock|copies)\\b.*"
        );
    }

    private boolean isBookDetailsQuestion(String message) {

        String normalized = normalize(message);

        return normalized.contains("tell me about")
                || normalized.contains("details about")
                || normalized.contains("details of")
                || normalized.contains("information about")
                || normalized.contains("information on")
                || normalized.contains("what do you know about")
                || normalized.startsWith("describe ");
    }

    private boolean isCatalogQuestion(String message) {

        String normalized = normalize(message);

        return normalized.matches(
                ".*\\b(all|every|list|show|display)\\b.*"
                        + ".*\\b(books|book|catalog|inventory)\\b.*"
        );
    }

    private boolean isCountQuestion(String message) {

        String normalized = normalize(message);

        return normalized.matches(
                ".*\\b(how many|count|number of)\\b.*"
                        + ".*\\b(books|book)\\b.*"
        );
    }

    private String normalize(String message) {

        return message
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean isIdentityQuestion(String message) {

        String normalized = normalize(message);

        return normalized.contains("my name")
                && (
                normalized.startsWith("what")
                        || normalized.startsWith("do you remember")
                        || normalized.startsWith("can you tell me")
        );
    }

    private Optional<String> findUserNameInConversation(
            String conversationId
    ) {

        List<Message> messages = chatMemory.get(conversationId);

        Pattern namePattern = Pattern.compile(
                "\\bmy\\s+name\\s+is\\s+([A-Za-z][A-Za-z'-]*(?:\\s+[A-Za-z][A-Za-z'-]*){0,2})",
                Pattern.CASE_INSENSITIVE
        );

        for (int i = messages.size() - 1; i >= 0; i--) {

            Message message = messages.get(i);

            if (message instanceof UserMessage userMessage) {

                Matcher matcher = namePattern.matcher(
                        userMessage.getText()
                );

                if (matcher.find()) {
                    return Optional.of(matcher.group(1).trim());
                }
            }
        }

        return Optional.empty();
    }
}
