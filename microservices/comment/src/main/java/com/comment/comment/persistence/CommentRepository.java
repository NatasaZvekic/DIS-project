package com.comment.comment.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface CommentRepository  extends ReactiveCrudRepository<CommentEntity, String> {
    Flux<CommentEntity> findByBookId(int bookId);
}