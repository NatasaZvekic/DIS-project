//package com.reader.reader;
//
//import com.reader.reader.persistence.ReaderEntity;
//import com.reader.reader.persistence.ReaderRepository;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
//import org.springframework.dao.DuplicateKeyException;
//import org.springframework.dao.OptimisticLockingFailureException;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.List;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.junit.Assert.*;
//
//@RunWith(SpringRunner.class)
//@DataMongoTest
//public class PersistenceTests {
//
//    @Autowired
//    private ReaderRepository repository;
//
//    private ReaderEntity savedEntity;
//
//    @Before
//   	public void setupDb() {
//   		repository.deleteAll();
//
//        ReaderEntity entity = new ReaderEntity(1, 1, "Natasa", "Zvekic");
//        savedEntity = repository.save(entity);
//
//        assertEquals(entity, savedEntity);
//    }
//
//
//    @Test
//    public void getByBookId() {
//
//        List<ReaderEntity> entityList = repository.findByBookId(savedEntity.getBookId());
//
//        assertEquals(1, entityList.size());
//    }
//
//    @Test
//   	public void create() {
//        ReaderEntity newEntity = new ReaderEntity(7, 1, "Natasa", "Zvekic");
//        repository.save(newEntity);
//
//        assertEquals(2, repository.count());
//    }
//
//    @Test
//   	public void update() {
//        savedEntity.setFirstName("Update");
//        repository.save(savedEntity);
//
//        ReaderEntity foundEntity = repository.findById(savedEntity.getId()).get();
//        assertEquals("Update", foundEntity.getFirstName());
//    }
//
//    @Test
//   	public void delete() {
//        repository.delete(savedEntity);
//        assertFalse(repository.existsById(savedEntity.getId()));
//    }
//}
