package com.book.book.perstistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface CrudRepostitory extends CrudRepository<BookEntity, String> {
    Optional<BookEntity> findByBookId(int bookId);
}
