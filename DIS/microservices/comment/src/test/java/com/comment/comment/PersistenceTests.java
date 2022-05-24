package com.comment.comment;

import com.comment.comment.persistence.CommentEntity;
import com.comment.comment.persistence.CommentRepository;
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
    private CommentRepository repository;

    private CommentEntity savedEntity;

    @Before
   	public void setupDb() {
   		repository.deleteAll();
        CommentEntity entity = new CommentEntity(1, 1, "Test comment");
        savedEntity = repository.save(entity);

        assertEquals(entity, savedEntity);
    }


    @Test
    public void getByBookId() {
        List<CommentEntity> entityList = repository.findByBookId(savedEntity.getBookId());
        assertEquals(1, entityList.size());
    }

    @Test
   	public void create() {
        CommentEntity newEntity = new CommentEntity(7, 1, "Test comment");
        repository.save(newEntity);

        assertEquals(2, repository.count());
    }

    @Test
   	public void update() {
        savedEntity.setComment("Update");
        repository.save(savedEntity);

        CommentEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals("Update", foundEntity.getComment());
    }

    @Test
   	public void delete() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }
}
