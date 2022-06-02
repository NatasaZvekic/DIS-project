package com.comment.comment.services;

import java.util.function.Consumer;
import core.comments.Comment;
import core.comments.CommentsService;
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

    private final CommentsService commentsService;

    @Autowired
    public MessageProcessorConfig(CommentsService commentsService) {
        this.commentsService = commentsService;
    }

    @Bean
    public Consumer<Event<Integer, Comment>> messageProcessor() {
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            switch (event.getEventType()) {

                case CREATE:
                    Comment comment = event.getData();
                    LOG.info("Create comment with ID: {}", comment.getBookId());
                    commentsService.createComment(comment).block();
                    break;

                case DELETE:
                    int bookId = event.getKey();
                    LOG.info("Delete comment with bookdId: {}", bookId);
                    commentsService.deleteComment(bookId).block();
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
