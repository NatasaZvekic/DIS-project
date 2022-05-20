package composite.book;

import core.comments.Comment;
import core.rates.Rate;
import core.readers.Reader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface BookCompositeService {

    @GetMapping(
            value    = "/book-composite/{bookId}",
            produces = "application/json")
    BookAggregate getBook(@PathVariable int bookId);
}
