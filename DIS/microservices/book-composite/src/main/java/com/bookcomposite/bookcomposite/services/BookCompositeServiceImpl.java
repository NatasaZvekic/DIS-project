package com.bookcomposite.bookcomposite.services;

import composite.book.BookAggregate;
import composite.book.BookCompositeService;
import core.book.Book;
import core.comments.Comment;
import core.rates.Rate;
import core.readers.Reader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import util.exceptions.NotFoundException;

import java.util.List;


@RestController
public class BookCompositeServiceImpl implements BookCompositeService {

    private  BookCompositeIntegration integration;

    @Autowired
    public BookCompositeServiceImpl( BookCompositeIntegration integration) {
        this.integration = integration;
    }



    @Override
    public BookAggregate getBook(int bookId) {
        Book book = integration.getBook(bookId);
        if (book == null) throw new NotFoundException("No book found for bookId: " + bookId);

        List<Reader> readers = integration.getReader(bookId);

        List<Rate> rates = integration.getRate(bookId);

        List<Comment> comments = integration.getComments(bookId);

        return new BookAggregate(bookId, comments, readers, rates, book.getBookName());

    }

}
