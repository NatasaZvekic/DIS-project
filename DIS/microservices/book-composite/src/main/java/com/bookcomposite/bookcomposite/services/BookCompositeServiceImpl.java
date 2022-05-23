package com.bookcomposite.bookcomposite.services;

import composite.book.BookAggregate;
import composite.book.BookCompositeService;
import core.book.Book;
import core.comments.Comment;
import core.rates.Rate;
import core.readers.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import util.exceptions.NotFoundException;

import java.util.List;


@RestController
public class BookCompositeServiceImpl implements BookCompositeService {

    private  BookCompositeIntegration integration;
    private static final Logger LOG = LoggerFactory.getLogger(BookCompositeServiceImpl.class);

    @Autowired
    public BookCompositeServiceImpl( BookCompositeIntegration integration) {
        this.integration = integration;
    }

    @Override
    public BookAggregate getBookComposite(int bookId) {
        Book book = integration.getBook(bookId);
        if (book == null) throw new NotFoundException("No book found for bookId: " + bookId);

        List<Reader> readers = integration.getReader(bookId);

        List<Rate> rates = integration.getRate(bookId);

        List<Comment> comments = integration.getComments(bookId);

        return new BookAggregate(bookId, comments, readers, rates, book.getBookName());

    }

    @Override
    public void createCompositeBook(BookAggregate body) {
        LOG.debug("createCompositeBook: creates a new composite entity for bookId: {}", body.getBookId());

        Book book = new Book(body.getBookId(), body.getName());
        integration.createBook(book);

        if (body.getComments() != null) {
            body.getComments().forEach(r -> {
                Comment comment = new Comment(body.getBookId(), r.getCommentId(), r.getComment());
                integration.createComment(comment);
            });
        }

        Reader reader2 = new Reader(body.getBookId(), 3, "d", "F");
        integration.createReader(reader2);

        if (body.getReaders() != null) {
            body.getReaders().forEach(r -> {
                Reader reader = new Reader(body.getBookId(), r.getReaderId(), r.getFirstName(), r.getLastName());
                integration.createReader(reader);
            });
        }

        if (body.getRates() != null) {
            body.getRates().forEach(r -> {
                Rate rate = new Rate(body.getBookId(), r.getRateId(), r.getRate());
                integration.createRate(rate);
            });
        }
    }

    @Override
    public void deleteCompositeBook(int bookId) {
        LOG.debug("deleteCompositeBook: Deletes a book aggregate for bookId: {}", bookId);

        integration.deleteBook(bookId);

        integration.deleteComment(bookId);

        integration.deleteReader(bookId);

        integration.deleteRate(bookId);

        LOG.debug("getCompositeBook: aggregate entities deleted for bookId: {}", bookId);
    }

}
