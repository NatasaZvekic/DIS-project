package com.book.book;

import com.book.book.perstistence.BookEntity;
import com.book.book.perstistence.BookRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MongoDBContainer;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
public class PersistenceTests extends MongoDbTestBase {

    @Autowired
    private BookRepository repository;

    private BookEntity savedEntity;

    @BeforeEach
    public void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();

        BookEntity entity = new BookEntity(1, "Test book");
        StepVerifier.create(repository.save(entity))
                .expectNextMatches(createdEntity -> {
                    savedEntity = createdEntity;
                    return areBookEqual(entity, savedEntity);
                })
                .verifyComplete();
    }


    @Test
    public void getBookById() {
        StepVerifier.create(repository.findByBookId(savedEntity.getBookId()))
                .expectNextMatches(foundEntity -> areBookEqual(savedEntity, foundEntity))
                .verifyComplete();
    }

    @Test
    public void create() {
        BookEntity entity = new BookEntity(1, "Test book");
        StepVerifier.create(repository.save(entity))
                .expectNextMatches(createdEntity -> entity.getBookId() == createdEntity.getBookId())
                .verifyComplete();

        StepVerifier.create(repository.existsById(entity.getId())).expectNext(true).verifyComplete();
    }

    @Test
    public void update() {
        savedEntity.setBookName("Updated name");
        StepVerifier.create(repository.save(savedEntity))
                .expectNextMatches(updatedEntity -> updatedEntity.getBookName().equals("Updated name"))
                .verifyComplete();

        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getBookName().equals("Updated name"))
                .verifyComplete();

    }

    @Test
    void delete() {
        StepVerifier.create(repository.delete(savedEntity)).verifyComplete();
        StepVerifier.create(repository.existsById(savedEntity.getId())).expectNext(false).verifyComplete();
    }

    private boolean areBookEqual(BookEntity expectedEntity, BookEntity actualEntity) {
        return
                (expectedEntity.getId().equals(actualEntity.getId()))
                        && (expectedEntity.getVersion() == actualEntity.getVersion())
                        && (expectedEntity.getBookId() == actualEntity.getBookId())
                        && (expectedEntity.getBookName().equals(actualEntity.getBookName()));
    }
}



