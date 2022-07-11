package com.reader.reader.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ReaderRepository extends ReactiveCrudRepository<ReaderEntity, String> {
    Flux<ReaderEntity> findByBookId(int bookId);
}