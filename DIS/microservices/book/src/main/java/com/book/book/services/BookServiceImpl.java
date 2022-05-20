package com.book.book.services;

import core.book.Book;
import core.book.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import util.exceptions.InvalidInputException;

@RestController
public class BookServiceImpl implements BookService {

    private static final Logger LOG = LoggerFactory.getLogger(BookServiceImpl.class);

    @Override
    public Book getBook(int bookId) {
        LOG.debug("No publisher found for publisherId={}", bookId);

        if (bookId < 1) throw new InvalidInputException("Invalid commentId: " + bookId);
        return new Book(bookId,  "X");

    }
}
