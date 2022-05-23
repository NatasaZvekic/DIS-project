package core.comments;

import org.springframework.web.bind.annotation.*;
import java.util.List;

public interface CommentsService {

    @GetMapping(
            value    = "/comment",
            produces = "application/json")
    List<Comment> getComments(@RequestParam(value = "bookId", required = true) int bookId);

    @PostMapping(
            value = "/comment",
            consumes = "application/json",
            produces = "application/json")
    Comment createComment(@RequestBody Comment body);

    @DeleteMapping(value = "/comment")
    void deleteComment(@RequestParam(value = "bookId", required = true)  int bookId);
}
