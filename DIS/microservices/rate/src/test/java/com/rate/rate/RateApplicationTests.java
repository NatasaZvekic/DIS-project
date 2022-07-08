package com.rate.rate;

import static event.Event.Type.CREATE;
import static event.Event.Type.DELETE;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import java.util.function.Consumer;
import com.rate.rate.persistence.RateRepository;
import com.rate.rate.services.RateServiceImpl;
import core.rates.Rate;
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
class RateApplicationTests extends MongoDbTestBase {

    @Autowired
    private RateRepository repository;

    @Autowired
    @Qualifier("messageProcessor")
    private Consumer<Event<Integer, Rate>> messageProcessor;

    @Autowired
    private RateServiceImpl service;

    @BeforeEach
    void setupDb() {
        repository.deleteAll().block();
    }

    @Test
    void getCommentById() {
        int bookId = 1;
        sendCreateRateEvent(bookId);
        assertNotNull(repository.findByBookId(bookId));
    }

    @Test
    void getCommentsById_throws_invalid_input() {
        int invalidBookId = 0;
        InvalidInputException thrown = assertThrows(
                InvalidInputException.class,
                () -> service.getRate(invalidBookId),
                "Expected a InvalidInputException here!");
        assertEquals("Invalid bookId", thrown.getMessage());
    }

//    @Test
//    void getCommentsById_throws_not_found() {
//        int invalidBookId = 234;
//        NotFoundException thrown = assertThrows(
//                NotFoundException.class,
//                () -> service.getRate(invalidBookId),
//                "Expected a NotFoundException here!");
//        assertEquals("Not found", thrown.getMessage());
//    }

    @Test
    void create(){
        int bookId = 1;
        sendCreateRateEvent(bookId);

        StepVerifier.create(repository.count())
                .assertNext(count -> {
                    assertTrue(count == 1);
                })
                .verifyComplete();
    }

    @Test
    void create_throws_duplicate_error() {
        int bookId = 1;
        sendCreateRateEvent(bookId);
        DuplicateKeyException thrown = assertThrows(
                DuplicateKeyException.class,
                () -> sendCreateRateEvent(bookId),
                "Expected a DuplicateException here!");
        assertEquals("Duplicate key", thrown.getMessage());
    }

    @Test
    void create_throws_invalid_error() {
        int boookId = 0;
        InvalidInputException thrown = assertThrows(
                InvalidInputException.class,
                () -> sendCreateRateEvent(boookId),
                "Expected a InvalidInputException here!");
        assertEquals("Invalid bookId", thrown.getMessage());
    }

    @Test
    void delete(){
        int bookId = 1;
        sendCreateRateEvent(bookId);
        sendDeleteRateEvent(bookId);
        assertEquals(0, (long)repository.count().block());
    }

    private void sendCreateRateEvent(int bookId) {
        Rate rate = new Rate(bookId,2, 8);
        Event<Integer, Rate> event = new Event(CREATE, bookId, rate);
        messageProcessor.accept(event);
    }

    private void sendDeleteRateEvent(int rateId) {
        Event<Integer, Rate> event = new Event(DELETE, rateId, null);
        messageProcessor.accept(event);
    }

}
