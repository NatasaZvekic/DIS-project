package com.rate.rate.services;

import com.rate.rate.persistence.RateEntity;
import com.rate.rate.persistence.RateRepository;
import core.comments.Comment;
import core.rates.Rate;
import core.rates.RateService;
import core.readers.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import util.exceptions.InvalidInputException;

import java.util.ArrayList;
import java.util.List;

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
    public List<Rate> getRate(int bookId) {
        LOG.debug("No genre found for rateId={}", bookId);

        if (bookId < 1) throw new InvalidInputException("Invalid bookId: " + bookId);

        List<RateEntity> entityList = repository.findByBookId(bookId);
        List<Rate> list = mapper.entityListToApiList(entityList);

        LOG.debug("/rates response size: {}", list.size());

        return list;
    }

    @Override
    public Rate createRate(Rate body) {
        try {
            RateEntity entity = mapper.apiToEntity(body);
            RateEntity newEntity = repository.save(entity);

            LOG.debug("createRate: created a rate entity: {}/{}", body.getBookId(), body.getRateId());
            return mapper.entityToApi(newEntity);

        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, bookId: " + body.getBookId() + ", rate Id:" + body.getRateId());
        }
    }

    @Override
    public void deleteRate(int bookId) {
        LOG.debug("deleteRates: tries to delete rates for the bookId with bookId: {}", bookId);
        repository.deleteAll(repository.findByBookId(bookId));
    }

}