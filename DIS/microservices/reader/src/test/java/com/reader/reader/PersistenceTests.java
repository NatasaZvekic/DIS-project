package com.reader.reader;

import com.reader.reader.persistence.ReaderEntity;
import com.reader.reader.persistence.ReaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;
import java.util.Objects;
import static org.junit.Assert.assertTrue;

@DataMongoTest
public class PersistenceTests extends MongoDbTestBase {

    @Autowired
    private ReaderRepository repository;


    private ReaderEntity savedEntity;

    @BeforeEach
    public void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();
    }

    @Test
    public void getReaderByBookId() {
        ReaderEntity reader = new ReaderEntity(1, 1, "test", "test");
        repository.save(reader).block();
        StepVerifier.create(repository.existsById(reader.getId())).expectNext(true).verifyComplete();
    }

    @Test
    public void create() {
        ReaderEntity entity = new ReaderEntity(1, 1, "test", "test");
        ReaderEntity entity2 = new ReaderEntity(1, 2, "test", "test");
        ReaderEntity entity3 = new ReaderEntity(1, 3, "test", "test");

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
        String updatedName = "update";
        ReaderEntity entity = new ReaderEntity(1, 1, "test", "test");
        repository.save(entity).block();
        entity.setFirstName(updatedName);
        repository.save(entity).block();

        StepVerifier.create(repository.findByBookId(entity.getBookId()))
                .assertNext(updatedEntity -> {
                    assertTrue(Objects.equals(updatedEntity.getFirstName(), updatedName));
                })
                .verifyComplete();
    }


    @Test
    void delete() {
        ReaderEntity entity = new ReaderEntity(1,1,  "test", "test");
        repository.save(entity).block();
        repository.delete(entity).block();
        StepVerifier.create(repository.existsById(entity.getId())).expectNext(false).verifyComplete();
    }

}