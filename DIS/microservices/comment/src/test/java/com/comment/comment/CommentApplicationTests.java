package com.comment.comment;

import static event.Event.Type.CREATE;
import static event.Event.Type.DELETE;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import java.util.function.Consumer;
import com.comment.comment.persistence.CommentRepository;
import com.comment.comment.services.CommentServiceImpl;
import core.comments.Comment;
import event.Event;
import exceptions.InvalidInputException;
import exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
        "eureka.client.enabled=false",
        "spring.sleuth.mongodb.enabled=false"})
class CommentApplicationTests extends MongoDbTestBase {

    @Autowired
    private CommentRepository repository;

    @Autowired
    @Qualifier("messageProcessor")
    private Consumer<Event<Integer, Comment>> messageProcessor;

    @Autowired
    private CommentServiceImpl service;

    @BeforeEach
    void setupDb() {
        repository.deleteAll().block();
    }

    @Test
    void getCommentById() {
        int bookId = 1;
        sendCreateCommentEvent(bookId);
        assertNotNull(repository.findByBookId(bookId));
    }

    @Test
    void getCommentsById_throws_invalid_input() {
        int invalidBookId = 0;
        InvalidInputException thrown = assertThrows(
                InvalidInputException.class,
                () -> service.getComments(invalidBookId),
                "Expected a InvalidInputException here!");
        assertEquals("Invalid bookId", thrown.getMessage());
    }

//    @Test
//    void getCommentsById_throws_not_found() {
//        int invalidBookId = 234;
//        NotFoundException thrown = assertThrows(
//                NotFoundException.class,
//                () -> service.getComments(invalidBookId),
//                "Expected a NotFoundException here!");
//        assertEquals("Not found", thrown.getMessage());
//    }

    @Test
    void create(){
        int bookId = 1;
        sendCreateCommentEvent(bookId);

        StepVerifier.create(repository.count())
                .assertNext(count -> {
                    assertTrue(count == 1);
                })
                .verifyComplete();
    }

    @Test
    void create_throws_duplicate_error() {
        int bookId = 1;
        sendCreateCommentEvent(bookId);
        DuplicateKeyException thrown = assertThrows(
                DuplicateKeyException.class,
                () -> sendCreateCommentEvent(bookId),
                "Expected a DuplicateException here!");
        assertEquals("Duplicate key", thrown.getMessage());
    }

    @Test
    void create_throws_invalid_error() {
        int boookId = 0;
        InvalidInputException thrown = assertThrows(
                InvalidInputException.class,
                () -> sendCreateCommentEvent(boookId),
                "Expected a InvalidInputException here!");
        assertEquals("Invalid bookId", thrown.getMessage());
    }

    @Test
    void delete(){
        int bookId = 1;
        sendCreateCommentEvent(bookId);
        sendDeleteCommentEvent(bookId);
        assertEquals(0, (long)repository.count().block());
    }

    private void sendCreateCommentEvent(int bookId) {
        Comment comment = new Comment(bookId,2, "Name");
        Event<Integer, Comment> event = new Event(CREATE, bookId, comment);
        messageProcessor.accept(event);
    }

    private void sendDeleteCommentEvent(int commentId) {
        Event<Integer, Comment> event = new Event(DELETE, commentId, null);
        messageProcessor.accept(event);
    }

}
