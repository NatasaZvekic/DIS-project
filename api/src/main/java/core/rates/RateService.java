package core.rates;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RateService {

    @GetMapping(
            value    = "/rate",
            produces = "application/json")
    Flux<Rate> getRate(@RequestParam(value = "bookId", required = true) int bookId);

    @PostMapping(
            value = "/rate",
            consumes = "application/json",
            produces = "application/json")
    Mono<Rate> createRate(@RequestBody Rate body);

    @DeleteMapping(value = "/rate")
    Mono<Void> deleteRate(@RequestParam(value = "bookId", required = true)  int bookId);
}
