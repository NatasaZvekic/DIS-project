package core.readers;

import core.rates.Rate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ReaderService {

    @GetMapping(
            value    = "/reader",
            produces = "application/json")
    List<Reader> getReader(@RequestParam(value = "bookId", required = true) int bookId);
}
