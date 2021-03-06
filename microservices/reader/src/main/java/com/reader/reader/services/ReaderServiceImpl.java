package com.reader.reader.services;

import com.reader.reader.persistence.ReaderEntity;
import com.reader.reader.persistence.ReaderRepository;
import core.readers.ReaderService;
import exceptions.InvalidInputException;
import exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import core.readers.Reader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Flux<Reader> getReader(int bookId) {
        if (bookId < 1)  {
            throw new InvalidInputException("Invalid bookId");
        }

        return repository.findByBookId(bookId)
                .log()
                .map(e -> mapper.entityToApi(e));
    }

    @Override
    public Mono<Reader> createReader(Reader body) {
        if (body.getBookId() < 1) throw new InvalidInputException("Invalid bookId");

        if(Boolean.FALSE.equals(repository.findByBookId(body.getBookId()).hasElements().block())) {
            ReaderEntity entity = mapper.apiToEntity(body);
            Mono<Reader> newEntity = repository.save(entity)
                    .log()
                    .onErrorMap(
                            DuplicateKeyException.class,
                            ex -> new InvalidInputException("Duplicate key, Book Id: " + body.getBookId() + ", Reader Id:" + body.getReaderId()))
                    .map(e -> mapper.entityToApi(e));

            return newEntity;
        }
        throw new DuplicateKeyException("Duplicate key");
    }

    @Override
    public Mono<Void> deleteReader(int bookId) {
        LOG.debug("deleteReaders: tries to delete readers for the bookId with bookId: {}", bookId);
        return repository.deleteAll(repository.findByBookId(bookId));
    }

}