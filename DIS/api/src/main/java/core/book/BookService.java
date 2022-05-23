package core.book;

import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface BookService {
    @GetMapping(
            value    = "/book/{bookId}",
            produces = "application/json")
    Book getBook(@PathVariable int bookId);

    @PostMapping(
            value = "/book",
            consumes = "application/json",
            produces = "application/json")
    Book createBook(@RequestBody Book body);
    @DeleteMapping(value = "/book/{bookId}")
    void deleteBook(@PathVariable int bookId);

}
