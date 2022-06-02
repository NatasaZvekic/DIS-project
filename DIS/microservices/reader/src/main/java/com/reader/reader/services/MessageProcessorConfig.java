package com.reader.reader.services;

import java.util.function.Consumer;

import core.comments.Comment;
import core.comments.CommentsService;
import core.rates.Rate;
import core.rates.RateService;
import core.readers.Reader;
import core.readers.ReaderService;
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

    private final ReaderService readerService;

    @Autowired
    public MessageProcessorConfig(ReaderService readerService) {
        this.readerService = readerService;
    }

    @Bean
    public Consumer<Event<Integer, Reader>> messageProcessor() {
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            switch (event.getEventType()) {

                case CREATE:
                    Reader reader = event.getData();
                    LOG.info("Create reader with ID: {}", reader.getBookId());
                    readerService.createReader(reader).block();
                    break;

                case DELETE:
                    int bookId = event.getKey();
                    LOG.info("Delete reader with bookdId: {}", bookId);
                    readerService.deleteReader(bookId).block();
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
