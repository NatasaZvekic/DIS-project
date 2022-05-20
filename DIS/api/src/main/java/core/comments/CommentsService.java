package core.comments;

import core.rates.Rate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CommentsService {

    @GetMapping(
            value    = "/comment",
            produces = "application/json")
    List<Comment> getComments(@RequestParam(value = "bookId", required = true) int bookId);
}
