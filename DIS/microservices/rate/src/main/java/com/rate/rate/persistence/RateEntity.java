package com.rate.rate.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="rates")
@CompoundIndex(name = "book-rate-id", unique = true, def = "{'bookId': 1, 'rateId' : 1}")
public class RateEntity {
    @Id
    private String id;
    @Version
    private Integer version;

    private int bookId;
    private int rateId;
    private int rate;


    public RateEntity() {
    }

    public RateEntity(String id, Integer version, int bookId, int rateId, int rate) {
        this.id = id;
        this.version = version;
        this.bookId = bookId;
        this.rateId = rateId;
        this.rate = rate;
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

    public int getRateId() {
        return rateId;
    }

    public void setRateId(int rateId) {
        this.rateId = rateId;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
