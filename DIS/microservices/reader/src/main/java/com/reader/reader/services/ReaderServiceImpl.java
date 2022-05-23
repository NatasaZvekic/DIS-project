package com.reader.reader.services;

import com.reader.reader.persistence.ReaderEntity;
import com.reader.reader.persistence.ReaderRepository;
import core.readers.ReaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import core.readers.Reader;
import util.exceptions.InvalidInputException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ReaderServiceImpl implements ReaderService {
    private final ReaderRepository repository;
    private final ReaderMapper mapper;
    private static final Logger LOG = LoggerFactory.getLogger(ReaderServiceImpl.class);

    public ReaderServiceImpl(ReaderRepository repository, ReaderMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Reader> getReader(int bookId) {
        LOG.debug("No user found for bookId={}", bookId);

        if (bookId < 1) throw new InvalidInputException("Invalid bookId: " + bookId);

        List<ReaderEntity> entityList = repository.findByBookId(bookId);
        List<Reader> list = mapper.entityListToApiList(entityList);

        LOG.debug("/readers response size: {}", list.size());

        return list;
    }

    @Override
    public Reader createReader(Reader body) {
        try {
            ReaderEntity entity = mapper.apiToEntity(body);
            ReaderEntity newEntity = repository.save(entity);

            LOG.debug("createReader: created a reader entity: {}/{}", body.getBookId(), body.getReaderId());
            return mapper.entityToApi(newEntity);

        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, bookId: " + body.getBookId() + ", reader Id:" + body.getReaderId());
        }
    }

    @Override
    public void deleteReader(int bookId) {
        LOG.debug("deleteReaders: tries to delete readers for the bookId with bookId: {}", bookId);
        repository.deleteAll(repository.findByBookId(bookId));
    }

}