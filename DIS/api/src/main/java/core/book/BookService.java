package core.book;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface BookService {
    @GetMapping(
            value    = "/book/{bookId}",
            produces = "application/json")
    Book getBook(@PathVariable int bookId);
}
