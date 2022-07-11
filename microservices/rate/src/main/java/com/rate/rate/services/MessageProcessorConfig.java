package com.rate.rate.services;

import java.util.function.Consumer;
import core.rates.Rate;
import core.rates.RateService;
import event.Event;
import exceptions.EventProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final RateService rateService;

    @Autowired
    public MessageProcessorConfig(RateService rateService) {
        this.rateService = rateService;
    }

    @Bean
    public Consumer<Event<Integer, Rate>> messageProcessor() {
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());

            switch (event.getEventType()) {

                case CREATE:
                    Rate rate = event.getData();
                    LOG.info("Create rate with ID: {}", rate.getBookId());
                    rateService.createRate(rate).block();
                    break;

                case DELETE:
                    int bookId = event.getKey();
                    LOG.info("Delete rate with bookdId: {}", bookId);
                    rateService.deleteRate(bookId).block();
                    break;

                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                    LOG.warn(errorMessage);
                    throw new EventProcessingException(errorMessage);
            }

            LOG.info("Message processing done!");

        };
    }
}
