package com.reader.reader.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReaderRepository extends CrudRepository<ReaderEntity, String> {
    List<ReaderEntity> findByBookId(int bookId);
}