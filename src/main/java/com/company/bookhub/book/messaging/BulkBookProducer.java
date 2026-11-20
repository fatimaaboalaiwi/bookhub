
package com.company.bookhub.book.messaging;

import com.company.bookhub.book.dto.BookRequest;
import com.company.bookhub.book.messaging.dto.BulkBookMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BulkBookProducer {

    private final RabbitTemplate rabbitTemplate;

    public BulkBookProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public BulkBookMessage publishBooks(List<BookRequest> books) {

        BulkBookMessage message = new BulkBookMessage(
                UUID.randomUUID(),
                List.copyOf(books)
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.BULK_BOOK_ROUTING_KEY,
                message
        );

        return message;
    }
}
