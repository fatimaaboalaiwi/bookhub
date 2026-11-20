
package com.company.bookhub.book.messaging;

import com.company.bookhub.book.messaging.dto.BulkBookMessage;
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
                message.books().size()
        );

        processingService.processBooks(message.books());

        log.info(
                "Successfully processed bulk book batch. Batch ID: {}",
                message.batchId()
        );
    }
}
