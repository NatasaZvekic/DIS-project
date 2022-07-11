package com.rate.rate.services;

import com.rate.rate.persistence.RateEntity;
import com.rate.rate.persistence.RateRepository;
import core.rates.Rate;
import core.rates.RateService;
import exceptions.InvalidInputException;
import exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class RateServiceImpl implements RateService {

    private final RateRepository repository;
    private final RateMapper mapper;
    private static final Logger LOG = LoggerFactory.getLogger(RateServiceImpl.class);

    public RateServiceImpl(RateRepository repository, RateMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Flux<Rate> getRate(int bookId) {
        if (bookId < 1)  {
            throw new InvalidInputException("Invalid bookId");
        }

        return repository.findByBookId(bookId)
                .log()
                .map(e -> mapper.entityToApi(e));
    }

    @Override
    public Mono<Rate> createRate(Rate body) {
        if (body.getBookId() < 1) throw new InvalidInputException("Invalid bookId");

        if(Boolean.FALSE.equals(repository.findByBookId(body.getBookId()).hasElements().block())) {
            RateEntity entity = mapper.apiToEntity(body);
            Mono<Rate> newEntity = repository.save(entity)
                    .log()
                    .onErrorMap(
                            DuplicateKeyException.class,
                            ex -> new InvalidInputException("Duplicate key, Book Id: " + body.getBookId() + ", Rate Id:" + body.getRateId()))
                    .map(e -> mapper.entityToApi(e));

            return newEntity;
        }
        throw new DuplicateKeyException("Duplicate key");
    }

    @Override
    public Mono<Void> deleteRate(int bookId) {
        LOG.debug("deleteRates: tries to delete rates for the bookId with bookId: {}", bookId);
        return repository.deleteAll(repository.findByBookId(bookId));
    }

}