package com.book.book.services;

import com.book.book.perstistence.BookEntity;
import com.book.book.perstistence.BookRepository;
import core.book.Book;
import core.book.BookService;
import exceptions.InvalidInputException;
import exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static java.util.logging.Level.FINE;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class BookServiceImpl implements BookService {
    private final BookRepository repository;
    private final BookMapper mapper;
    private static final Logger LOG = LoggerFactory.getLogger(BookServiceImpl.class);

    public BookServiceImpl(BookRepository repository, BookMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Book> getBook(int bookId) {
        if (bookId < 1) {
            throw new InvalidInputException("Invalid bookId");
        }

        return repository.findByBookId(bookId)
                .switchIfEmpty(
                        Mono.error(new NotFoundException("No book found for bookId: " + bookId))
                )
                .log(LOG.getName(), FINE)
                .map(e -> mapper.entityToApi(e));
    }

    private Throwable handleException(Throwable ex) {

        if (!(ex instanceof WebClientResponseException)) {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }

        WebClientResponseException wcre = (WebClientResponseException) ex;

        switch (wcre.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException((wcre));

            case UNPROCESSABLE_ENTITY:
                return new InvalidInputException((wcre));

            default:
                LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
                LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
                return ex;
        }
    }


    @Override
    public Mono<Book> createBook(Book body) {
        if (body.getBookId() < 1) throw new InvalidInputException("Invalid bookId");
        if(!repository.findByBookId(body.getBookId()).hasElement().block()){
            BookEntity entity = mapper.apiToEntity(body);
            Mono<Book> newEntity = repository.save(entity)
                    .log()
                    .map(e -> mapper.entityToApi(e));

            return newEntity;
        }
        throw new DuplicateKeyException("Duplicate key");
    }


    @Override
    public Mono<Void> deleteBook(int bookId) {
        LOG.debug("deleteBook: tries to delete an entity with bookId: {}", bookId);
        return repository.findByBookId(bookId).log(LOG.getName(), FINE).map(e -> repository.delete(e)).flatMap(e -> e);

      //  return repository.findByBookId(bookId).log().map(e -> repository.delete(e)).flatMap(e -> e);
    }
}
