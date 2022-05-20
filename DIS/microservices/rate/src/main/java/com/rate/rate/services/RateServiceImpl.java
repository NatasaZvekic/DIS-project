package com.rate.rate.services;

import core.rates.Rate;
import core.rates.RateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import util.exceptions.InvalidInputException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RateServiceImpl implements RateService {

    private static final Logger LOG = LoggerFactory.getLogger(RateServiceImpl.class);

    @Override
    public List<Rate> getRate(int bookId) {
        LOG.debug("No genre found for rateId={}", bookId);

        if (bookId < 1) throw new InvalidInputException("Invalid bookId: " + bookId);

        List<Rate> list = new ArrayList<>();
        list.add(new Rate(bookId,1,  4));
        list.add(new Rate(bookId,2,  8));
        list.add(new Rate(bookId,3,  7));

        LOG.debug("/rates response size: {}", list.size());

        return list;
    }

}