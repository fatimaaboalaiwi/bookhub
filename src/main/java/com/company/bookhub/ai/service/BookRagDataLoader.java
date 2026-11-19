
package com.company.bookhub.ai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class BookRagDataLoader implements ApplicationRunner {

    private static final Logger log =
            LoggerFactory.getLogger(BookRagDataLoader.class);

    private final BookRagIndexingService indexingService;

    public BookRagDataLoader(
            BookRagIndexingService indexingService
    ) {
        this.indexingService = indexingService;
    }

    @Override
    public void run(ApplicationArguments args) {

        log.info("Starting BookHub RAG indexing...");

        int indexedBooks = indexingService.indexBooks();

        log.info(
                "BookHub RAG indexing completed. Indexed {} books.",
                indexedBooks
        );
    }
}
