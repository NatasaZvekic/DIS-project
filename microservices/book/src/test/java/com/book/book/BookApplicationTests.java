package com.book.book;

import static event.Event.Type.CREATE;
import static event.Event.Type.DELETE;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import java.util.function.Consumer;
import com.book.book.perstistence.BookRepository;
import com.book.book.services.BookServiceImpl;
import core.book.Book;
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
class BookApplicationTests extends MongoDbTestBase {

    @Autowired
    private BookRepository repository;

    @Autowired
    @Qualifier("messageProcessor")
    private Consumer<Event<Integer, Book>> messageProcessor;

    @Autowired
    private BookServiceImpl bookService;

    @BeforeEach
    void setupDb() {
        repository.deleteAll().block();
    }

    @Test
    void getBookById() {
        int bookId = 1;
        sendCreateBookEvent(bookId);
        assertNotNull(repository.findByBookId(bookId).block());
    }

    @Test
    void getBookById_throws_invalid_input() {
        int invalidBookId = 0;
        InvalidInputException thrown = assertThrows(
                InvalidInputException.class,
                () -> bookService.getBook(invalidBookId),
                "Expected a InvalidInputException here!");
        assertEquals("Invalid bookId", thrown.getMessage());
    }

    @Test
    void create(){
        int bookId = 1;
        sendCreateBookEvent(bookId);

        StepVerifier.create(repository.count())
                .assertNext(count -> {
                    assertTrue(count == 1);
                })
                .verifyComplete();
    }

    @Test
    void create_throws_duplicate_error() {
        int bookId = 1;
        sendCreateBookEvent(bookId);
        DuplicateKeyException thrown = assertThrows(
                DuplicateKeyException.class,
                () -> sendCreateBookEvent(bookId),
                "Expected a DuplicateException here!");
        assertEquals("Duplicate key", thrown.getMessage());
    }

    @Test
    void create_throws_invalid_error() {
        int boookId = 0;
        InvalidInputException thrown = assertThrows(
                InvalidInputException.class,
                () -> sendCreateBookEvent(boookId),
                "Expected a InvalidInputException here!");
        assertEquals("Invalid bookId", thrown.getMessage());
    }

    @Test
    void delete(){
        int bookId = 1;
        sendCreateBookEvent(bookId);
        sendDeleteBookEvent(bookId);
        assertEquals(0, (long)repository.count().block());
    }

    private void sendCreateBookEvent(int bookId) {
        Book book = new Book(bookId, "Name");
        Event<Integer, Book> event = new Event(CREATE, bookId, book);
        messageProcessor.accept(event);
    }

    private void sendDeleteBookEvent(int bookId) {
        Event<Integer, Book> event = new Event(DELETE, bookId, null);
        messageProcessor.accept(event);
    }

}
