
package com.company.bookhub.book.messaging;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "bookhub.exchange";

    public static final String BULK_BOOK_QUEUE = "bookhub.books.bulk";
    public static final String BULK_BOOK_ROUTING_KEY = "books.bulk";

    public static final String NOTIFICATION_QUEUE = "bookhub.notifications";
    public static final String NOTIFICATION_ROUTING_KEY = "books.notification";

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public DirectExchange bookHubExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue bulkBookQueue() {
        return new Queue(BULK_BOOK_QUEUE, true);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Binding bulkBookBinding(
            Queue bulkBookQueue,
            DirectExchange bookHubExchange
    ) {
        return BindingBuilder
                .bind(bulkBookQueue)
                .to(bookHubExchange)
                .with(BULK_BOOK_ROUTING_KEY);
    }

    @Bean
    public Binding notificationBinding(
            Queue notificationQueue,
            DirectExchange bookHubExchange
    ) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(bookHubExchange)
                .with(NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public ApplicationRunner declareRabbitMQResources(AmqpAdmin amqpAdmin) {
        return args -> {
            if (amqpAdmin instanceof RabbitAdmin rabbitAdmin) {
                rabbitAdmin.initialize();
            }
        };
    }
}
