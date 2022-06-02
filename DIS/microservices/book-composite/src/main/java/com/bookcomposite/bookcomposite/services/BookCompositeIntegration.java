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
import event.Event;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import util.exceptions.InvalidInputException;
import util.exceptions.NotFoundException;
import util.exceptions.http.HttpErrorInfo;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import java.io.IOException;
import org.slf4j.Logger;
import static event.Event.Type.CREATE;
import static event.Event.Type.DELETE;
import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;

@Component
public class BookCompositeIntegration implements RateService, ReaderService, CommentsService, BookService {

    private static final Logger LOG = LoggerFactory.getLogger(BookCompositeIntegration.class);

    private final WebClient webClient;

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String readerServiceUrl;
    private final String rateServiceUrl;
    private final String commentServiceUrl;
    private final String bookServiceUrl;

    private final Scheduler publishEventScheduler;
    private final StreamBridge streamBridge;

    @Autowired
    public BookCompositeIntegration(
            @Qualifier("publishEventScheduler") Scheduler publishEventScheduler,
            WebClient.Builder webClient,
            ObjectMapper mapper,
            RestTemplate restTemplate,
            StreamBridge streamBridge,
            @Value("${app.book.host}") String bookServiceHost,
            @Value("${app.book.port}") int bookServicePort,

            @Value("${app.rate.host}") String rateServiceHost,
            @Value("${app.rate.port}") int rateServicePort,

            @Value("${app.reader.host}") String readerServiceHost,
            @Value("${app.reader.port}") int readerServicePort,

            @Value("${app.comment.host}") String commentServiceHost,
            @Value("${app.comment.port}") int commentServicePort
    ) {
        this.publishEventScheduler = publishEventScheduler;
        this.restTemplate = restTemplate;
        this.webClient = webClient.build();
        this.mapper = mapper;
        this.streamBridge = streamBridge;

        bookServiceUrl = "http://" + bookServiceHost + ":" + bookServicePort;
        rateServiceUrl = "http://" + rateServiceHost + ":" + rateServicePort + "/rate";
        commentServiceUrl = "http://" + commentServiceHost + ":" + commentServicePort + "/comment";
        readerServiceUrl = "http://" + readerServiceHost + ":" + readerServicePort + "/reader";
    }

    @Override
    public Mono<Book> getBook(int bookId) {
        String url = bookServiceUrl + "/book/" + bookId;
        LOG.debug("Will call getBook API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToMono(Book.class).log().onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
    }

    @Override
    public Mono<Book> createBook(Book body) {
        return Mono.fromCallable(() -> {
            sendMessage("books-out-0", new Event(CREATE, body.getBookId(), body));
            return body;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> deleteBook(int bookId) {
        return Mono.fromRunnable(() -> sendMessage("books-out-0", new Event(DELETE, bookId, null)))
                .subscribeOn(publishEventScheduler).then();
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

    @Override
    public Flux<Comment> getComments(int bookId) {
        String url = commentServiceUrl + "?bookId=" + bookId;
        LOG.debug("Will call the getComments API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(Comment.class).log().onErrorResume(error -> empty());
    }

    @Override
    public Mono<Comment> createComment(Comment body) {
        return Mono.fromCallable(() -> {
            sendMessage("comments-out-0", new Event(CREATE, body.getBookId(), body));
            return body;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> deleteComment(int bookId) {
        return Mono.fromRunnable(() -> sendMessage("comments-out-0", new Event(DELETE, bookId, null)))
                .subscribeOn(publishEventScheduler).then();
    }

    @Override
    public Flux<Rate> getRate(int bookId) {
        String url = rateServiceUrl + "?bookId=" + bookId;
        LOG.debug("Will call getRates API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(Rate.class).log().onErrorResume(error -> empty());
    }

    @Override
    public Mono<Rate> createRate(Rate body) {
        return Mono.fromCallable(() -> {
            sendMessage("rates-out-0", new Event(CREATE, body.getBookId(), body));
            return body;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> deleteRate(int bookId) {
        return Mono.fromRunnable(() -> sendMessage("rates-out-0", new Event(DELETE, bookId, null)))
                .subscribeOn(publishEventScheduler).then();
    }

    @Override
    public Flux<Reader> getReader(int bookId) {
        String url = readerServiceUrl + "?bookId=" + bookId;
        LOG.debug("Will call getReader API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(Reader.class).log().onErrorResume(error -> empty());
    }

    @Override
    public Mono<Reader> createReader(Reader body) {
        return Mono.fromCallable(() -> {
            sendMessage("readers-out-0", new Event(CREATE, body.getBookId(), body));
            return body;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> deleteReader(int bookId) {
        return Mono.fromRunnable(() -> sendMessage("readers-out-0", new Event(DELETE, bookId, null)))
                .subscribeOn(publishEventScheduler).then();
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(ex));

            case UNPROCESSABLE_ENTITY:
                return new InvalidInputException(getErrorMessage(ex));

            default:
                LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                return ex;
        }
    }

    private Throwable handleException(Throwable ex) {

        if (!(ex instanceof WebClientResponseException)) {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }

        WebClientResponseException wcre = (WebClientResponseException) ex;

        switch (wcre.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(wcre));

            case UNPROCESSABLE_ENTITY:
                return new InvalidInputException(getErrorMessage(wcre));

            default:
                LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
                LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
                return ex;
        }
    }

    private String getErrorMessage(WebClientResponseException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

    private void sendMessage(String bindingName, Event event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getKey())
                .build();
        streamBridge.send(bindingName, message);
    }

    public Mono<Health> getBookHealth() {
        return getHealth("http://" + "book" + ":" + "8080");
    }
    public Mono<Health> getRateHealth() {
        return getHealth("http://" + "rate" + ":" + "8080");
    }
    public Mono<Health> getReaderHealth() {
        return getHealth("http://" + "reader" + ":" + "8080");
    }

    public Mono<Health> getCommentHealth() {
        return getHealth("http://" + "comment" + ":" + "8080");
    }

    private Mono<Health> getHealth(String url) {
        url += "/actuator/health";
        LOG.debug("Will call the Health API on URL: {}", url);
        return webClient.get().uri(url).retrieve().bodyToMono(String.class)
                .map(s -> new Health.Builder().up().build())
                .onErrorResume(ex -> Mono.just(new
                        Health.Builder().down(ex).build()))
                .log(LOG.getName(), FINE);
    }
}
