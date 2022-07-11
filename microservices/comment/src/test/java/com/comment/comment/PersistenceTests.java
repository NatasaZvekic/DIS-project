package com.comment.comment;

import com.comment.comment.persistence.CommentEntity;
import com.comment.comment.persistence.CommentRepository;
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
    private CommentRepository repository;


    private CommentEntity savedEntity;

    @BeforeEach
    public void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();
    }

    @Test
    public void getCommentByBookId() {
        CommentEntity comment = new CommentEntity(1, 1, "comm1");
        repository.save(comment).block();
        StepVerifier.create(repository.existsById(comment.getId())).expectNext(true).verifyComplete();
    }

    @Test
    public void create() {
        CommentEntity entity = new CommentEntity(1, 1,"comm1");
        CommentEntity entity2 = new CommentEntity(1, 2, "comm2");
        CommentEntity entity3 = new CommentEntity(1, 3, "comm3");
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
        CommentEntity entity = new CommentEntity(1, 1, "Test book");
        repository.save(entity).block();
        entity.setComment(updatedName);
        repository.save(entity).block();

        StepVerifier.create(repository.findByBookId(entity.getBookId()))
                .assertNext(updatedEntity -> {
                    assertTrue(Objects.equals(updatedEntity.getComment(), "Updated name"));
                })
                .verifyComplete();
    }


    @Test
    void delete() {
        CommentEntity entity = new CommentEntity(1,1,  "Test book");
        repository.save(entity).block();
        repository.delete(entity).block();
        StepVerifier.create(repository.existsById(entity.getId())).expectNext(false).verifyComplete();
    }

}