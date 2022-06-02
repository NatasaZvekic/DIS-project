package com.rate.rate.persistence;


import reactor.core.publisher.Flux;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import java.util.List;

public interface RateRepository extends ReactiveCrudRepository<RateEntity, String> {
    Flux<RateEntity> findByBookId(int bookId);
}