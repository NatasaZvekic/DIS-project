package com.book.book.services;

import com.book.book.perstistence.BookEntity;
import com.book.book.perstistence.BookRepository;
import com.book.book.perstistence.CrudRepostitory;
import core.book.Book;
import core.book.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import util.exceptions.InvalidInputException;
import util.exceptions.NotFoundException;
import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Mono.error;

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
        LOG.debug("No book found for bookId={}", bookId);
        if (bookId < 1) throw new InvalidInputException("Invalid bookId: " + bookId);

        return repository.findByBookId(bookId)
                .switchIfEmpty(error(new NotFoundException("No book found for bookId: " + bookId)))
                .log()
                .map(e -> mapper.entityToApi(e));
    }

    @Override
    public Mono<Book> createBook(Book body) {

        if (body.getBookId() < 1) throw new InvalidInputException("Invalid bookId: " + body.getBookId());
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
