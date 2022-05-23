package com.comment.comment.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository  extends CrudRepository<CommentEntity, String> {
    List<CommentEntity> findByBookId(int bookId);
}