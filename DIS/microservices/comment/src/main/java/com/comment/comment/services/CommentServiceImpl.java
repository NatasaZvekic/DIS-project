package com.comment.comment.services;

import core.comments.Comment;
import core.comments.CommentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import util.exceptions.InvalidInputException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CommentServiceImpl implements CommentsService {

    private static final Logger LOG = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Override
    public List<Comment> getComments(int bookId) {
        LOG.debug("No publisher found for publisherId={}", bookId);

        if (bookId < 1) throw new InvalidInputException("Invalid commentId: " + bookId);

        List<Comment> list = new ArrayList<>();
        list.add(new Comment(bookId,1,  "X"));
        list.add(new Comment(bookId,2,  "X"));
        list.add(new Comment(bookId,3,  "X"));

        LOG.debug("/comments response size: {}", list.size());

        return list;
    }

}