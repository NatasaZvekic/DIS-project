package core.comments;

public class Comment {
    private int bookId;
    private int commentId;
    private String comment;

    public Comment() {
    }

    public Comment(int bookId, int commentId, String comment) {
        this.bookId = bookId;
        this.commentId = commentId;
        this.comment = comment;
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
