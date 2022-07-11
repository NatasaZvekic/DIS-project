package com.reader.reader;

import static event.Event.Type.CREATE;
import static event.Event.Type.DELETE;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import java.util.function.Consumer;
import com.reader.reader.persistence.ReaderRepository;
import com.reader.reader.services.ReaderServiceImpl;
import core.readers.Reader;
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
class ReaderApplicationTests extends MongoDbTestBase {

    @Autowired
    private ReaderRepository repository;

    @Autowired
    @Qualifier("messageProcessor")
    private Consumer<Event<Integer, Reader>> messageProcessor;

    @Autowired
    private ReaderServiceImpl service;

    @BeforeEach
    void setupDb() {
        repository.deleteAll().block();
    }

    @Test
    void getCommentById() {
        int bookId = 1;
        sendCreateReaderEvent(bookId);
        assertNotNull(repository.findByBookId(bookId));
    }

    @Test
    void getCommentsById_throws_invalid_input() {
        int invalidBookId = 0;
        InvalidInputException thrown = assertThrows(
                InvalidInputException.class,
                () -> service.getReader(invalidBookId),
                "Expected a InvalidInputException here!");
        assertEquals("Invalid bookId", thrown.getMessage());
    }

    //    @Test
    //    void getCommentsById_throws_not_found() {
    //        int invalidBookId = 234;
    //        NotFoundException thrown = assertThrows(
    //                NotFoundException.class,
    //                () -> service.getReader(invalidBookId),
    //                "Expected a NotFoundException here!");
    //        assertEquals("Not found", thrown.getMessage());
    //    }

    @Test
    void create(){
        int bookId = 1;
        sendCreateReaderEvent(bookId);

        StepVerifier.create(repository.count())
                .assertNext(count -> {
                    assertTrue(count == 1);
                })
                .verifyComplete();
    }

    @Test
    void create_throws_duplicate_error() {
        int bookId = 1;
        sendCreateReaderEvent(bookId);
        DuplicateKeyException thrown = assertThrows(
                DuplicateKeyException.class,
                () -> sendCreateReaderEvent(bookId),
                "Expected a DuplicateException here!");
        assertEquals("Duplicate key", thrown.getMessage());
    }

    @Test
    void create_throws_invalid_error() {
        int boookId = 0;
        InvalidInputException thrown = assertThrows(
                InvalidInputException.class,
                () -> sendCreateReaderEvent(boookId),
                "Expected a InvalidInputException here!");
        assertEquals("Invalid bookId", thrown.getMessage());
    }

    @Test
    void delete(){
        int bookId = 1;
        sendCreateReaderEvent(bookId);
        sendDeleteReaderEvent(bookId);
        assertEquals(0, (long)repository.count().block());
    }

    private void sendCreateReaderEvent(int bookId) {
        Reader reader = new Reader(bookId,2, "test", "test");
        Event<Integer, Reader> event = new Event(CREATE, bookId, reader);
        messageProcessor.accept(event);
    }

    private void sendDeleteReaderEvent(int readerId) {
        Event<Integer, Reader> event = new Event(DELETE, readerId, null);
        messageProcessor.accept(event);
    }

}
