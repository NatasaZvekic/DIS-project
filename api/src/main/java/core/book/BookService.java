package core.book;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BookService {
    @GetMapping(
            value    = "/book/{bookId}",
            produces = "application/json")
    Mono<Book> getBook(@PathVariable int bookId);

    @PostMapping(
            value = "/book",
            consumes = "application/json",
            produces = "application/json")
    Mono<Book> createBook(@RequestBody Book body);
  //  @DeleteMapping(value = "/book/{bookId}")
    Mono<Void> deleteBook(@PathVariable int bookId);

}
