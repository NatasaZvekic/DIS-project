package core.readers;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReaderService {

    @GetMapping(
            value    = "/reader",
            produces = "application/json")
    Flux<Reader> getReader(@RequestParam(value = "bookId", required = true) int bookId);

    @PostMapping(
            value = "/reader",
            consumes = "application/json",
            produces = "application/json")
    Mono<Reader> createReader(@RequestBody Reader body);

    @DeleteMapping(value = "/reader")
    Mono<Void> deleteReader(@RequestParam(value = "bookId", required = true)  int bookId);
}