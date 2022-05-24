package com.reader.reader.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="readers")
@CompoundIndex(name = "book-read-id", unique = true, def = "{'bookId': 1, 'readerId' : 1}")
public class ReaderEntity {
    @Id
    private String id;
    @Version
    private Integer version;

    private int bookId;

    private int readerId;
    private String firstName;
    private String lastName;

    public ReaderEntity() {
    }

    public ReaderEntity(int bookId, int readerId, String firstName, String lastName) {
        this.bookId = bookId;
        this.readerId = readerId;
        this.firstName = firstName;
        this.lastName = lastName;
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
