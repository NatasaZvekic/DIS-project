package com.comment.comment.services;

import com.comment.comment.persistence.CommentEntity;
import com.comment.comment.persistence.CommentRepository;
import core.comments.Comment;
import core.comments.CommentsService;
import core.rates.Rate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
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
    public List<Comment> getComments(int bookId) {
        LOG.debug("No publisher found for publisherId={}", bookId);

        if (bookId < 1) throw new InvalidInputException("Invalid commentId: " + bookId);

        List<CommentEntity> entityList = repository.findByBookId(bookId);
        List<Comment> list = mapper.entityListToApiList(entityList);

        LOG.debug("/comments response size: {}", list.size());

        return list;
    }

    @Override
    public Comment createComment(Comment body) {
        try {
            CommentEntity entity = mapper.apiToEntity(body);
            CommentEntity newEntity = repository.save(entity);

            LOG.debug("createComment: created a comment entity: {}/{}", body.getBookId(), body.getCommentId());
            return mapper.entityToApi(newEntity);

        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, bookId: " + body.getBookId() + ", reader Id:" + body.getCommentId());
        }
    }

    @Override
    public void deleteComment(int bookId) {
        LOG.debug("deleteComment: tries to delete comment for the bookId with bookId: {}", bookId);
        repository.deleteAll(repository.findByBookId(bookId));
    }

}