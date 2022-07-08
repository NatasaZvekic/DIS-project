package com.bookcomposite.bookcomposite;

import com.bookcomposite.bookcomposite.services.BookCompositeIntegration;
import com.bookcomposite.bookcomposite.services.BookCompositeServiceImpl;
import core.book.Book;
import core.comments.Comment;
import core.rates.Rate;
import core.readers.Reader;
import exceptions.InvalidInputException;
import exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = {TestSecurityConfig.class},
        properties = {
                "spring.security.oauth2.resourceserver.jwt.issuer-uri=",
                "spring.main.allow-bean-definition-overriding=true",
                "eureka.client.enabled=false",
                "spring.cloud.config.enabled=false"})
class BookCompositeApplicationTests {
    private static final int BOOK_ID_OK = 30;
    private static final int BOOK_ID_NOT_FOUND = 2;
    private static final int BOOK_ID_INVALID = 7;

    @Autowired
    private WebTestClient client;

    @Autowired
    private BookCompositeServiceImpl service;

    @MockBean
    private BookCompositeIntegration compositeIntegration;

    @BeforeEach
    void setUp() {
        when(compositeIntegration.getBook(BOOK_ID_OK)).thenReturn(Mono.just(new Book(BOOK_ID_OK, "test")));
        when(compositeIntegration.getReader(BOOK_ID_OK)).thenReturn(Flux.fromIterable(singletonList(new Reader(BOOK_ID_OK, 1, "test", "test"))));
        when(compositeIntegration.getRate(BOOK_ID_OK)).thenReturn(Flux.fromIterable(singletonList(new Rate(BOOK_ID_OK, 1, 5))));
        when(compositeIntegration.getComments(BOOK_ID_OK)).thenReturn(Flux.fromIterable(singletonList(new Comment(BOOK_ID_OK, 1, "comment"))));
        when(compositeIntegration.getBook(BOOK_ID_INVALID)).thenThrow(new InvalidInputException("Invalid bookId"));
        when(compositeIntegration.getComments(BOOK_ID_INVALID)).thenThrow(new InvalidInputException("Invalid bookId"));
        when(compositeIntegration.getRate(BOOK_ID_INVALID)).thenThrow(new InvalidInputException("Invalid bookId"));
        when(compositeIntegration.getReader(BOOK_ID_INVALID)).thenThrow(new InvalidInputException("Invalid bookId"));

        when(compositeIntegration.getBook(BOOK_ID_NOT_FOUND)).thenThrow(new NotFoundException("Not found" ));
    }

    @Test
    void contextLoads() {
    }

    @Test
    void getBookById() {
        assertNotNull(service.getBookComposite(BOOK_ID_OK).block());
    }
    @Test
    void getBookNotFound() {
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> service.getBookComposite(BOOK_ID_NOT_FOUND),
                "Expected a InvalidInputException here!");
        assertEquals("Not found", thrown.getMessage());
    }

    @Test
    void getBookInvalidInput() {
        InvalidInputException thrown = assertThrows(
                InvalidInputException.class,
                () -> service.getBookComposite(BOOK_ID_INVALID),
                "Expected a InvalidInputException here!");
        assertEquals("Invalid bookId", thrown.getMessage());
    }

}
