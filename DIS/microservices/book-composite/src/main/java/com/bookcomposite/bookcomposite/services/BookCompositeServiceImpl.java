package com.bookcomposite.bookcomposite.services;

import composite.book.BookAggregate;
import composite.book.BookCompositeService;
import core.book.Book;
import core.comments.Comment;
import core.rates.Rate;
import core.readers.Reader;
import exceptions.InvalidInputException;
import exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static java.util.logging.Level.FINE;


@RestController
public class BookCompositeServiceImpl implements BookCompositeService {

    private BookCompositeIntegration integration;
    private static final Logger LOG = LoggerFactory.getLogger(BookCompositeServiceImpl.class);

    @Autowired
    public BookCompositeServiceImpl(BookCompositeIntegration integration) {
        this.integration = integration;
    }

    @Override
    public Mono<BookAggregate> getBookComposite(int bookId) {
        return Mono.zip(
                        values -> createBookAggregate((Book) values[0], (List<Comment>) values[1], (List<Reader>) values[2], (List<Rate>) values[3]),
                        integration.getBook(bookId),
                        integration.getComments(bookId).collectList(),
                        integration.getReader(bookId).collectList(),
                        integration.getRate(bookId).collectList())
                .doOnError(ex -> LOG.warn("getBookComposite failed: {}", ex.toString()))
                .onErrorMap(WebClientResponseException.class, ex -> handleException(ex))
                .log();

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
    public Mono<Void> createCompositeBook(BookAggregate body) {
        try {
            List<Mono> monoList = new ArrayList<>();
            LOG.debug("createCompositeBook: creates a new composite entity for bookId: {}", body.getBookId());

            Book book = new Book(body.getBookId(), body.getName());
            monoList.add(integration.createBook(book));

            if (body.getComments() != null) {
                body.getComments().forEach(r -> {
                    Comment comment = new Comment(body.getBookId(), r.getCommentId(), r.getComment());
                    monoList.add(integration.createComment(comment));
                });
            }

//            Reader reader2 = new Reader(body.getBookId(), 3, "d", "F");
//            integration.createReader(reader2);

            if (body.getReaders() != null) {
                body.getReaders().forEach(r -> {
                    Reader reader = new Reader(body.getBookId(), r.getReaderId(), r.getFirstName(), r.getLastName());
                    monoList.add(integration.createReader(reader));
                });
            }

            if (body.getRates() != null) {
                body.getRates().forEach(r -> {
                    Rate rate = new Rate(body.getBookId(), r.getRateId(), r.getRate());
                    monoList.add(integration.createRate(rate));
                });
            }

            return Mono.zip(r -> "", monoList.toArray(new Mono[0]))
                    .doOnError(ex -> LOG.warn("createCompositeBook failed: {}", ex.toString()))
                    .then();


        } catch (RuntimeException re) {
            LOG.warn("createCompositeBook failed: {}", re.toString());
            throw re;
        }
    }

    @Override
    public Mono<Void> deleteCompositeBook(int bookId) {
        try {
            LOG.debug("deleteCompositeBook: Deletes a book aggregate for bookId: {}", bookId);

            return Mono.zip(
                            r -> "",
                            integration.deleteBook(bookId),
                            integration.deleteComment(bookId),
                            integration.deleteRate(bookId),
                            integration.deleteReader(bookId))
                    .doOnError(ex -> LOG.warn("delete failed: {}", ex.toString()))
                    .log(LOG.getName(), FINE).then();

        } catch (RuntimeException re) {
            LOG.warn("deleteCompositeBook failed: {}", re.toString());
            throw re;
        }
    }

    private BookAggregate createBookAggregate(Book book, List<Comment> comments, List<Reader> readers, List<Rate> rates) {

        int bookId = book.getBookId();
        String name = book.getBookName();

        List<Comment> commentsList = (comments == null) ? null :
                comments.stream()
                        .map(r -> new Comment(r.getBookId(), r.getCommentId(), r.getComment()))
                        .collect(Collectors.toList());

        List<Reader> readersList = (readers == null) ? null :
                readers.stream()
                        .map(r -> new Reader(r.getBookId(), r.getReaderId(), r.getFirstName(), r.getLastName()))
                        .collect(Collectors.toList());

        List<Rate> ratesList = (rates == null) ? null :
                rates.stream()
                        .map(r -> new Rate(r.getBookId(), r.getRateId(), r.getRate()))
                        .collect(Collectors.toList());

        return new BookAggregate(bookId, comments, readers, rates, name);
    }
}
