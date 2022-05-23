package core.readers;

import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface ReaderService {

    @GetMapping(
            value    = "/reader",
            produces = "application/json")
    List<Reader> getReader(@RequestParam(value = "bookId", required = true) int bookId);

    @PostMapping(
            value = "/reader",
            consumes = "application/json",
            produces = "application/json")
    Reader createReader(@RequestBody Reader body);

    @DeleteMapping(value = "/reader")
    void deleteReader(@RequestParam(value = "bookId", required = true)  int bookId);
}