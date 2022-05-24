package com.comment.comment.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="comments")
@CompoundIndex(name = "book-comm-id", unique = true, def = "{'bookId': 1, 'commentId' : 1}")
public class CommentEntity {

    @Id
    private String id;
    @Version
    private Integer version;

    private int bookId;
    private int commentId;
    private String comment;

    public CommentEntity() {
    }

    public CommentEntity(int bookId, int commentId, String comment) {
        this.bookId = bookId;
        this.commentId = commentId;
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
