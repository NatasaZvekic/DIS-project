package com.rate.rate.persistence;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface RateRepository extends CrudRepository<RateEntity, String> {
    List<RateEntity> findByBookId(int bookId);
}