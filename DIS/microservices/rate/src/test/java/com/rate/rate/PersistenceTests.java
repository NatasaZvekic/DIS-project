package com.rate.rate;


import com.rate.rate.persistence.RateEntity;
import com.rate.rate.persistence.RateRepository;
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
    private RateRepository repository;


    private RateEntity savedEntity;

    @BeforeEach
    public void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();
    }

    @Test
    public void getRateByBookId() {
        RateEntity comment = new RateEntity(1, 1, 4);
        repository.save(comment).block();
        StepVerifier.create(repository.existsById(comment.getId())).expectNext(true).verifyComplete();
    }

    @Test
    public void create() {
        RateEntity entity = new RateEntity(1, 1,5);
        RateEntity entity2 = new RateEntity(1, 2, 2);
        RateEntity entity3 = new RateEntity(1, 3, 9);
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
        int updatedRate = 10;
        RateEntity entity = new RateEntity(1, 1, 9);
        repository.save(entity).block();
        entity.setRate(updatedRate);
        repository.save(entity).block();

        StepVerifier.create(repository.findByBookId(entity.getBookId()))
                .assertNext(updatedEntity -> {
                    assertTrue(Objects.equals(updatedEntity.getRate(), updatedRate));
                })
                .verifyComplete();
    }


    @Test
    void delete() {
        RateEntity entity = new RateEntity(1,1,  4);
        repository.save(entity).block();
        repository.delete(entity).block();
        StepVerifier.create(repository.existsById(entity.getId())).expectNext(false).verifyComplete();
    }

}