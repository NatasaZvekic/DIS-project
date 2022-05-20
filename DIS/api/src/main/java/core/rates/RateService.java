package core.rates;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface RateService {

    @GetMapping(
            value    = "/rate",
            produces = "application/json")
    List<Rate> getRate(@RequestParam(value = "bookId", required = true) int bookId);
}
