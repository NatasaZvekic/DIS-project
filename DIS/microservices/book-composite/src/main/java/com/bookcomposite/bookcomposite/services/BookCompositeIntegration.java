package com.bookcomposite.bookcomposite.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.book.Book;
import core.book.BookService;
import core.comments.Comment;
import core.comments.CommentsService;
import core.rates.Rate;
import core.rates.RateService;
import core.readers.Reader;
import core.readers.ReaderService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import util.exceptions.InvalidInputException;
import util.exceptions.NotFoundException;
import util.exceptions.http.HttpErrorInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.http.HttpMethod.GET;
@Component
public class BookCompositeIntegration implements RateService, ReaderService, CommentsService, BookService {

    private static final Logger LOG = LoggerFactory.getLogger(BookCompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String readerServiceUrl;
    private final String rateServiceUrl;
    private final String commentServiceUrl;
    private final String bookServiceUrl;

    @Autowired
    public BookCompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,

            @Value("${app.book.host}") String bookServiceHost,
            @Value("${app.book.port}") int    bookServicePort,

            @Value("${app.rate.host}") String rateServiceHost,
            @Value("${app.rate.port}") int    rateServicePort,

            @Value("${app.reader.host}") String readerServiceHost,
            @Value("${app.reader.port}") int    readerServicePort,

            @Value("${app.comment.host}") String commentServiceHost,
            @Value("${app.comment.port}") int    commentServicePort
    ) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;

        bookServiceUrl        = "http://" + bookServiceHost + ":" + bookServicePort + "/book/";
        rateServiceUrl        = "http://" + rateServiceHost + ":" + rateServicePort + "/rate";
        commentServiceUrl = "http://" + commentServiceHost + ":" + commentServicePort + "/comment";
        readerServiceUrl         = "http://" + readerServiceHost + ":" + readerServicePort + "/reader";
    }

    @Override
    public Book getBook(int bookId) {
        try {
            String url = bookServiceUrl + "/" + bookId;
            LOG.debug("Will call getBook API on URL: {}", url);

            Book book = restTemplate.getForObject(url, Book.class);
            LOG.debug("Found a book with id: {}", book.getBookId());

            return book;

        } catch (HttpClientErrorException ex) {

            switch (ex.getStatusCode()) {

                case NOT_FOUND:
                    throw new NotFoundException(getErrorMessage(ex));

                case UNPROCESSABLE_ENTITY :
                    throw new InvalidInputException(getErrorMessage(ex));

                default:
                    LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                    LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                    throw ex;
            }
        }
    }

    @Override
    public Book createBook(Book body) {
        try {
            String url = bookServiceUrl;
            LOG.debug("Will post a new book to URL: {}", url);

            Book book = restTemplate.postForObject(url, body, Book.class);
            LOG.debug("Created a probookduct with id: {}", book.getBookId());

            return book;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteBook(int bookId) {
        try {
            restTemplate.delete(bookServiceUrl + "/" + bookId);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

    @Override
    public List<Comment> getComments(int bookId) {
        String url = commentServiceUrl + "?bookId=" + bookId;
        try {
            LOG.debug("Will call getComments API on URL: {}", url);
            List<Comment> books = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Comment>>() {}).getBody();

            LOG.debug("Found {} comments for a book with id: {}", books.size(), bookId);
            return books;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting comments " + url + " +, return zero comments: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Comment createComment(Comment body) {
        try {
            String url = commentServiceUrl;
            LOG.debug("Will post a new comment to URL: {}", url);

            Comment comment = restTemplate.postForObject(url, body, Comment.class);
            LOG.debug("Created a comment with id: {}", comment.getCommentId());

            return comment;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteComment(int bookId) {
        try {
            String url = commentServiceUrl + "?bookId=" + bookId;
            LOG.debug("Will call the deleteComment API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public List<Rate> getRate(int bookId) {
        try {
            String url = rateServiceUrl + "?bookId=" + bookId;

            LOG.debug("Will call getRates API on URL: {}", url);
            List<Rate> rates = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Rate>>() {}).getBody();

            LOG.debug("Found {} rates for a book with id: {}", rates.size(), bookId);
            return rates;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting rates, return zero rates: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Rate createRate(Rate body) {
        try {
            String url = rateServiceUrl;
            LOG.debug("Will post a new comment to URL: {}", url);

            Rate rate = restTemplate.postForObject(url, body, Rate.class);
            LOG.debug("Created a comment with id: {}", rate.getRateId());

            return rate;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteRate(int bookId) {
        try {
            String url = rateServiceUrl + "?bookId=" + bookId;
            LOG.debug("Will call the deleteRate API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public List<Reader> getReader(int bookId) {
        try {
            String url = readerServiceUrl  + "?bookId=" + bookId;

            LOG.debug("Will call getReader API on URL: {}", url);
            List<Reader> readers = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Reader>>() {}).getBody();

            LOG.debug("Found {} readers for a book with id: {}", readers.size(), bookId);
            return readers;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting readers, return zero readers: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Reader createReader(Reader body) {
        try {
            String url = readerServiceUrl;
            LOG.debug("Will post a new comment to URL: {}", url);

            Reader reader = restTemplate.postForObject(url, body, Reader.class);
            LOG.debug("Created a reader with id: {}", reader.getReaderId());

            return reader;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteReader(int bookId) {
        try {
            String url = readerServiceUrl + "?bookId=" + bookId;
            LOG.debug("Will call the deleteReader API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(ex));

            case UNPROCESSABLE_ENTITY :
                return new InvalidInputException(getErrorMessage(ex));

            default:
                LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                return ex;
        }
    }

}
