package composite.book;

import core.comments.Comment;
import core.rates.Rate;
import core.readers.Reader;

import java.util.List;

public class BookAggregate {
    private int bookId;
    private List<Comment> comments;
    private List<Reader> readers;
    private List<Rate> rates;
    private String name;

    public BookAggregate() {
    }

    public BookAggregate(int bookId, List<Comment> comments, List<Reader> readers, List<Rate> rates, String name) {
        this.bookId = bookId;
        this.comments = comments;
        this.readers = readers;
        this.rates = rates;
        this.name = name;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Reader> getReaders() {
        return readers;
    }

    public void setReaders(List<Reader> readers) {
        this.readers = readers;
    }

    public List<Rate> getRates() {
        return rates;
    }

    public void setRates(List<Rate> rates) {
        this.rates = rates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
