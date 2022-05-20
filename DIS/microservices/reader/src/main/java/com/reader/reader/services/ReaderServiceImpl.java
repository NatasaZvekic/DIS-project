package com.reader.reader.services;

import core.readers.ReaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import core.readers.Reader;
import util.exceptions.InvalidInputException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ReaderServiceImpl implements ReaderService {

    private static final Logger LOG = LoggerFactory.getLogger(ReaderServiceImpl.class);

    @Override
    public List<Reader> getReader(int bookId) {
        LOG.debug("No user found for bookId={}", bookId);

        if (bookId < 1) throw new InvalidInputException("Invalid bookId: " + bookId);

        List<Reader> list = new ArrayList<>();
        list.add(new Reader(bookId, 1, "name-" + bookId, "lastName- " + bookId));
        list.add(new Reader(bookId, 2, "name-" + bookId, "lastName- " + bookId));
        list.add(new Reader(bookId,3,  "name-" + bookId, "lastName- " + bookId));

        LOG.debug("/readers response size: {}", list.size());

        return list;
    }

}