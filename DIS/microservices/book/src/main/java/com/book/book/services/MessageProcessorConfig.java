package com.book.book.services;

import java.util.function.Consumer;

import core.book.Book;
import core.book.BookService;
import event.Event;
import exceptions.EventProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final BookService bookService;

    @Autowired
    public MessageProcessorConfig(BookService bookService) {
        this.bookService = bookService;
    }

    @Bean
    public Consumer<Event<Integer, Book>> messageProcessor() {
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            switch (event.getEventType()) {

                case CREATE:
                    Book book = event.getData();
                    LOG.info("Create book with ID: {}", book.getBookId());
                    bookService.createBook(book).block();
                    break;

                case DELETE:
                    int bookId = event.getKey();
                    LOG.info("Delete book with bookId: {}", bookId);
                    bookService.deleteBook(bookId).block();
                    break;

                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                    LOG.warn(errorMessage);
                    throw new EventProcessingException(errorMessage);
            }

            LOG.info("Message processing done!");

        };
    }
}
