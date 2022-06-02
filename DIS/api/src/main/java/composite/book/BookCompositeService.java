package composite.book;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

public interface BookCompositeService {

    @GetMapping(
            value    = "/book-composite/{bookId}",
            produces = "application/json")
    Mono<BookAggregate> getBookComposite(@PathVariable int bookId);

    @PostMapping(
            value    = "/book-composite",
            consumes = "application/json")
    Mono<Void> createCompositeBook(@RequestBody BookAggregate body);

    @DeleteMapping(value = "/book-composite/{bookId}")
    Mono<Void> deleteCompositeBook(@PathVariable int bookId);
}
