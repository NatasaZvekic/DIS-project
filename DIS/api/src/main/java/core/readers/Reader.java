package core.readers;

public class Reader {
    private int bookId;

    private int readerId;
    private String firstName;
    private String lastName;

    public Reader() {
    }

    public Reader(int bookId, int readerId, String firstName, String lastName) {
        this.bookId = bookId;
        this.readerId = readerId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getReaderId() {
        return readerId;
    }

    public void setReaderId(int readerId) {
        this.readerId = readerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
