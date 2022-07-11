package core.comments;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CommentsService {

    @GetMapping(
            value    = "/comment",
            produces = "application/json")
    Flux<Comment> getComments(@RequestParam(value = "bookId", required = true) int bookId);

    @PostMapping(
            value = "/comment",
            consumes = "application/json",
            produces = "application/json")
    Mono<Comment> createComment(@RequestBody Comment body);

    @DeleteMapping(value = "/comment")
    Mono<Void> deleteComment(@RequestParam(value = "bookId", required = true)  int bookId);
}
