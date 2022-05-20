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
        rateServiceUrl        = "http://" + rateServiceHost + ":" + rateServicePort + "/rate?bookId=";
        commentServiceUrl = "http://" + commentServiceHost + ":" + commentServicePort + "/comment?bookId=";
        readerServiceUrl         = "http://" + readerServiceHost + ":" + readerServicePort + "/reader?bookId=";
    }

    @Override
    public Book getBook(int bookId) {
        try {
            String url = bookServiceUrl + bookId;
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

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

    @Override
    public List<Comment> getComments(int bookId) {
        String url = commentServiceUrl + bookId;
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
    public List<Rate> getRate(int bookId) {
        try {
            String url = rateServiceUrl + bookId;

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
    public List<Reader> getReader(int bookId) {
        try {
            String url = readerServiceUrl + bookId;

            LOG.debug("Will call getReader API on URL: {}", url);
            List<Reader> readers = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Reader>>() {}).getBody();

            LOG.debug("Found {} readers for a book with id: {}", readers.size(), bookId);
            return readers;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting readers, return zero readers: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }
}
