package com.book.book.services;

import com.book.book.perstistence.BookEntity;
import com.book.book.perstistence.BookRepository;
import core.book.Book;
import core.book.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import util.exceptions.InvalidInputException;
import util.exceptions.NotFoundException;

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
    public Book getBook(int bookId) {
        LOG.debug("No publisher found for publisherId={}", bookId);

        if (bookId < 1) throw new InvalidInputException("Invalid commentId: " + bookId);

        BookEntity entity = repository.findByBookId(bookId)
                .orElseThrow(() -> new NotFoundException("No book found for bookId: " + bookId));

        Book response = mapper.entityToApi(entity);

        LOG.debug("getBook: found bookId: {}", response.getBookId());

        return response;
    }

    @Override
    public Book createBook(Book body) {
        try {
            BookEntity entity = mapper.apiToEntity(body);
            BookEntity newEntity = repository.save(entity);

            LOG.debug("createBook: entity created for bookId: {}", body.getBookId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, book Id: " + body.getBookId());
        }
    }

    @Override
    public void deleteBook(int bookId) {
        LOG.debug("deleteBook: tries to delete an entity with bookId: {}", bookId);
        repository.findByBookId(bookId).ifPresent(e -> repository.delete(e));
    }
}
