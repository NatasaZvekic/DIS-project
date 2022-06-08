package com.book.book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class  BookApplication {
	private static final Logger LOG = LoggerFactory.getLogger(BookApplication.class);
	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(BookApplication.class, args);
		String mongodDbHost = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
		String mongodDbPort = ctx.getEnvironment().getProperty("spring.data.mongodb.port");
		LOG.info("Connected to MongoDb: " + mongodDbHost + ":" + mongodDbPort);
	}

}
