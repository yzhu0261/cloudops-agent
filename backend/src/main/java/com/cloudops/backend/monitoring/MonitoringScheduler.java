package com.cloudops.backend.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@ConditionalOnProperty(name = "cloudops.monitoring.enabled", havingValue = "true")
public class MonitoringScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(MonitoringScheduler.class);

    private final MonitoringProperties properties;
    private final MetricCollectionService collectionService;

    public MonitoringScheduler(
            MonitoringProperties properties,
            MetricCollectionService collectionService
    ) {
        this.properties = properties;
        this.collectionService = collectionService;
    }

    @Scheduled(
            initialDelay = 5000,
            fixedDelayString = "${cloudops.monitoring.interval-ms:60000}"
    )
    public void runCycle() {
        MetricWindow window = MetricWindow.latestCompleted(Instant.now());
        log.info("Monitoring cycle started, window={} ~ {}, functions={}",
                window.start(), window.end(), properties.functions());

        for (String functionName : properties.functions()) {
            try {
                collectionService.collect(functionName, window);
            } catch (Exception e) {
                log.error("{}: collection failed, will retry next cycle - {}",
                        functionName, e.getMessage());
            }
        }
    }
}