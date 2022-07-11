package com.book.book.perstistence;

import core.book.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface BookRepository extends ReactiveCrudRepository<BookEntity, String> {
        Mono<BookEntity> findByBookId(int bookId);

}
