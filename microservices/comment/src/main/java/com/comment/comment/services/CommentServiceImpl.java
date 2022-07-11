package com.comment.comment.services;

import com.comment.comment.persistence.CommentEntity;
import com.comment.comment.persistence.CommentRepository;
import core.comments.Comment;
import core.comments.CommentsService;
import exceptions.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        if (bookId < 1)  {
            throw new InvalidInputException("Invalid bookId");
        }

        return repository.findByBookId(bookId)
                .log()
                .map(mapper::entityToApi);
    }

    @Override
    public Mono<Comment> createComment(Comment body) {
        if (body.getBookId() < 1) throw new InvalidInputException("Invalid bookId");

        if(Boolean.FALSE.equals(repository.findByBookId(body.getBookId()).hasElements().block())) {
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