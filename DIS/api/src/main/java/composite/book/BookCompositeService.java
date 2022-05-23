package composite.book;

import org.springframework.web.bind.annotation.*;

public interface BookCompositeService {

    @GetMapping(
            value    = "/book-composite/{bookId}",
            produces = "application/json")
    BookAggregate getBookComposite(@PathVariable int bookId);

    @PostMapping(
            value    = "/book-composite",
            consumes = "application/json")
    void createCompositeBook(@RequestBody BookAggregate body);

    @DeleteMapping(value = "/book-composite/{bookId}")
    void deleteCompositeBook(@PathVariable int bookId);
}
