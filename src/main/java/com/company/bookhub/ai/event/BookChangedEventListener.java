
package com.company.bookhub.ai.event;

import com.company.bookhub.ai.service.BookRagIndexingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookChangedEventListener {

    private final BookRagIndexingService indexingService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleBookChanged(BookChangedEvent event) {

        try {

            switch (event.operation()) {

                case UPSERT ->
                        indexingService.indexBook(event.bookId());

                case DELETE ->
                        indexingService.deleteBook(event.bookId());
            }

            log.info(
                    "Book RAG synchronization completed. Book ID: {}, Operation: {}",
                    event.bookId(),
                    event.operation()
            );

        } catch (Exception exception) {

            log.error(
                    "Book RAG synchronization failed. Book ID: {}, Operation: {}",
                    event.bookId(),
                    event.operation(),
                    exception
            );
        }
    }
}
