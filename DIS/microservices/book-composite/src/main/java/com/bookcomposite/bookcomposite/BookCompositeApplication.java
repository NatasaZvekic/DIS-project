package com.bookcomposite.bookcomposite;

import com.bookcomposite.bookcomposite.services.BookCompositeIntegration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
public class BookCompositeApplication {

	private final Integer threadPoolSize;
	private final Integer taskQueueSize;

	private static final Logger LOG = LoggerFactory.getLogger(BookCompositeApplication.class);

	@Autowired
	public BookCompositeApplication(
			@Value("${app.threadPoolSize:10}") Integer threadPoolSize,
			@Value("${app.taskQueueSize:100}") Integer taskQueueSize
	) {
		this.threadPoolSize = threadPoolSize;
		this.taskQueueSize = taskQueueSize;
	}

	@Bean
	public Scheduler publishEventScheduler() {
		LOG.info("Creates a messagingScheduler with connectionPoolSize = {}", threadPoolSize);
		return Schedulers.newBoundedElastic(threadPoolSize, taskQueueSize, "publish-pool");
	}

	@Autowired
	BookCompositeIntegration integration;
	@Bean
	ReactiveHealthContributor coreServices() {
		final Map<String, ReactiveHealthIndicator> registry = new LinkedHashMap<>();
		registry.put("book", () -> integration.getBookHealth());
		registry.put("comment", () -> integration.getCommentHealth());
		registry.put("rate", () -> integration.getRateHealth());
		registry.put("reader", () -> integration.getReaderHealth());

		return CompositeReactiveHealthContributor.fromMap(registry);
	}

	@Bean
	@LoadBalanced
	public WebClient.Builder loadBalancedWebClientBuilder() {
		return WebClient.builder();
	}

	public static void main(String[] args) {
		SpringApplication.run(BookCompositeApplication.class, args);
	}

}
