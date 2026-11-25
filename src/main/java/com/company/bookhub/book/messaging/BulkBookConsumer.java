
package com.company.bookhub.book.messaging;

import com.company.bookhub.book.messaging.dto.BulkBookMessage;
import com.company.bookhub.book.service.BulkBookProcessingResult;
import com.company.bookhub.book.service.BulkBookProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class BulkBookConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(BulkBookConsumer.class);

    private final BulkBookProcessingService processingService;

    public BulkBookConsumer(BulkBookProcessingService processingService) {
        this.processingService = processingService;
    }

    @RabbitListener(queues = RabbitMQConfig.BULK_BOOK_QUEUE)
    public void consumeBulkBooks(BulkBookMessage message) {

        log.info(
                "Received bulk book batch. Batch ID: {}, Number of books: {}",
                message.batchId(),
                message.books() == null ? 0 : message.books().size()
        );

        BulkBookProcessingResult result = processingService.processBatch(
                message.batchId(),
                message.books()
        );

        if (result.alreadyProcessed()) {
            log.warn(
                    "Duplicate batch ignored. Batch ID: {}",
                    message.batchId()
            );
            return;
        }

        log.info(
                "Bulk book batch completed. Batch ID: {}, Received: {}, Added: {}, Failed: {}",
                message.batchId(),
                result.receivedCount(),
                result.addedCount(),
                result.failedCount()
        );

        for (var failure : result.failures()) {
            log.warn(
                    "Book {} failed in batch {}. ISBN: {}, Errors: {}",
                    failure.bookNumber(),
                    message.batchId(),
                    failure.isbn(),
                    failure.errors()
            );
        }
    }
}
