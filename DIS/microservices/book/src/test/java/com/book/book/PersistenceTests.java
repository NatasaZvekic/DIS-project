package com.book.book;

import com.book.book.perstistence.BookEntity;
import com.book.book.perstistence.BookRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {

    @Autowired
    private BookRepository repository;

    private BookEntity savedEntity;

    @Before
   	public void setupDb() {
   		repository.deleteAll();
        BookEntity entity = new BookEntity(1, "Test book");
        savedEntity = repository.save(entity);

        assertEquals(entity, savedEntity);
    }


    @Test
    public void getBookById() {
        Optional<BookEntity> entityList = repository.findByBookId(savedEntity.getBookId());
        assertTrue(entityList.isPresent());
    }

    @Test
   	public void create() {
        BookEntity newEntity = new BookEntity(7, "Test book");
        repository.save(newEntity);

        assertEquals(2, repository.count());
    }

    @Test
   	public void update() {
        savedEntity.setBookName("Update");
        repository.save(savedEntity);

        BookEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals("Update", foundEntity.getBookName());
    }

    @Test
   	public void delete() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }
}
