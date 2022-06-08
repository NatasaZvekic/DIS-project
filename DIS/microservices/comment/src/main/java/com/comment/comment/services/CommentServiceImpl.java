package com.comment.comment.services;

import com.comment.comment.persistence.CommentEntity;
import com.comment.comment.persistence.CommentRepository;
import core.comments.Comment;
import core.comments.CommentsService;
import core.rates.Rate;
import core.readers.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import util.exceptions.InvalidInputException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CommentServiceImpl implements CommentsService {

    private final CommentRepository repository;
    private final CommentMapper mapper;

    private static final Logger LOG = LoggerFactory.getLogger(CommentServiceImpl.class);

    public CommentServiceImpl(CommentRepository repository, CommentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Flux<Comment> getComments(int bookId) {
        LOG.debug("No comment found for commentId={}", bookId);

        if (bookId < 1) throw new InvalidInputException("Invalid commentId: " + bookId);

        return repository.findByBookId(bookId)
                .log()
                .map(e -> mapper.entityToApi(e));
    }

    @Override
    public Mono<Comment> createComment(Comment body) {
        if(!repository.findByBookId(body.getBookId()).hasElements().block()) {
            CommentEntity entity = mapper.apiToEntity(body);
            Mono<Comment> newEntity = repository.save(entity)
                    .log()
                    .onErrorMap(
                            DuplicateKeyException.class,
                            ex -> new InvalidInputException("Duplicate key, Book Id: " + body.getBookId() + ", Comment Id:" + body.getCommentId()))
                    .map(e -> mapper.entityToApi(e));

            return newEntity;
        }
        throw new DuplicateKeyException("Duplicate key");
    }

    @Override
    public Mono<Void> deleteComment(int bookId) {
        LOG.debug("deleteComment: tries to delete comment for the bookId with bookId: {}", bookId);
       return repository.deleteAll(repository.findByBookId(bookId));
    }

}