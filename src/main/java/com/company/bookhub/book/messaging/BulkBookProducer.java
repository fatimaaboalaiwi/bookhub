
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

}
