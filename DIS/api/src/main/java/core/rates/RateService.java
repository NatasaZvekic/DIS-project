package core.rates;

import org.springframework.web.bind.annotation.*;
import java.util.List;

public interface RateService {

    @GetMapping(
            value    = "/rate",
            produces = "application/json")
    List<Rate> getRate(@RequestParam(value = "bookId", required = true) int bookId);

    @PostMapping(
            value = "/rate",
            consumes = "application/json",
            produces = "application/json")
    Rate createRate(@RequestBody Rate body);

    @DeleteMapping(value = "/rate")
    void deleteRate(@RequestParam(value = "bookId", required = true)  int bookId);
}
