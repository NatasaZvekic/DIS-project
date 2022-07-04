package com.book.book;

import com.book.book.perstistence.BookEntity;
import com.book.book.perstistence.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;
import org.springframework.dao.DuplicateKeyException;


import java.util.Objects;

import static org.junit.Assert.*;

@DataMongoTest
public class PersistenceTests extends MongoDbTestBase {

    @Autowired
    private BookRepository repository;

    @BeforeEach
    public void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();
    }

    @Test
    public void getBookById() {
        BookEntity book = new BookEntity(1, "test");
        repository.save(book).block();
        StepVerifier.create(repository.existsById(book.getId())).expectNext(true).verifyComplete();

    }

//    @Test
//    public void getZeroBookById() {
//        BookEntity book = new BookEntity(1, "test");
//        repository.save(book).block();
//        StepVerifier.create(repository.existsById(book.getId())).expectNext(true).verifyComplete();
//
//    }
    @Test
    public void create() {
        BookEntity entity = new BookEntity(1, "Test book");
        BookEntity entity2 = new BookEntity(2, "Test book");
        BookEntity entity3 = new BookEntity(3, "Test book");
        repository.save(entity).block();
        repository.save(entity2).block();
        repository.save(entity3).block();
        StepVerifier.create(repository.count())
                .assertNext(count -> {
                    assertTrue(count == 3);
                })
                .verifyComplete();
    }

    @Test
    public void update() {
        String updatedName = "Updated name";
        BookEntity entity = new BookEntity(1, "Test book");
        repository.save(entity);
        entity.setBookName(updatedName);
        repository.save(entity);

        StepVerifier.create(repository.findById(entity.getId()))
                .assertNext(updatedEntity -> {
                    assertTrue(Objects.equals(updatedEntity.getBookName(), updatedName));
                })
                .verifyComplete();
    }

    @Test
    public void createWithDuplicatedIds(){
        BookEntity entity = new BookEntity(4, "Test book");
        BookEntity entity2 = new BookEntity(4, "Test book");
        repository.save(entity).block();
        repository.save(entity2).block();
        StepVerifier.create(repository.count())
                .assertNext(count -> {
                    assertTrue(count == 1);
                })
                .verifyComplete();//        StepVerifier.create(repository.count())
//                .assertNext(count -> {
//                    assertTrue(count == 2);
//                })
//                .verifyComplete();
    }

    @Test
    void delete() {
        BookEntity entity = new BookEntity(1, "Test book");
        repository.save(entity).block();
        repository.delete(entity).block();
        StepVerifier.create(repository.existsById(entity.getId())).expectNext(false).verifyComplete();
    }

}



