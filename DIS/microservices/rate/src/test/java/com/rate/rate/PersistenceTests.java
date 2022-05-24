package com.rate.rate;

import com.rate.rate.persistence.RateEntity;
import com.rate.rate.persistence.RateRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {

    @Autowired
    private RateRepository repository;

    private RateEntity savedEntity;

    @Before
   	public void setupDb() {
   		repository.deleteAll();
        RateEntity entity = new RateEntity(1, 1, 8);
        savedEntity = repository.save(entity);

        assertEquals(entity, savedEntity);
    }


    @Test
    public void getByBookId() {
        List<RateEntity> entityList = repository.findByBookId(savedEntity.getBookId());
        assertEquals(1, entityList.size());
    }

    @Test
   	public void create() {
        RateEntity newEntity = new RateEntity(7, 1, 10);
        repository.save(newEntity);

        assertEquals(2, repository.count());
    }

    @Test
   	public void update() {
        savedEntity.setRate(10);
        repository.save(savedEntity);

        RateEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(10, foundEntity.getRate());
    }

    @Test
   	public void delete() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }
}
