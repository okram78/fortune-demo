package dev.okram.demo.fortuneservice;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class FortuneGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(FortuneGenerator.class);

    private final ApplicationEventPublisher eventPublisher;

    private final AtomicLong idGenerator = new AtomicLong();

    private final Random random = new Random();

    private ScheduledExecutorService scheduler;


    public FortuneGenerator(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void start() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        LOGGER.info("Scheduling fortune generation...");
        scheduler.schedule(this::publishFortuneEvent, 5, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void stop() {
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
    }

    private void publishFortuneEvent() {
        try {
            String fortune = getFortuneFromCommand();
            long id = idGenerator.incrementAndGet();
            LOGGER.info("Publishing fortune event with id: {}", id);
            eventPublisher.publishEvent(new FortuneEvent(this, id, fortune));
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred while publishing fortune event", e);
        }

        int delay = getRandomInterval();
        LOGGER.info("Scheduling next fortune generation in {} seconds...", delay);
        scheduler.schedule(this::publishFortuneEvent, delay, TimeUnit.SECONDS);
    }

    private String getFortuneFromCommand() {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("fortune");
            Process process= processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            process.waitFor();
        } catch (Exception e) {
            stringBuilder.append("Error fetching fortune: ").append(e.getMessage());

            LOGGER.error("Error occurred during fortune command execution.", e);
        }
        return stringBuilder.toString();
    }

    private int getRandomInterval() {
        return 5 + random.nextInt(10);
    }
}
