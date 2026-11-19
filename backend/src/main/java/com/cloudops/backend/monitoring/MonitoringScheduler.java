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

    private static final int MAX_WINDOWS_PER_CYCLE = 12;

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
        MetricWindow latest = MetricWindow.latestCompleted(Instant.now());
        log.info("Monitoring cycle started, latest window={} ~ {}, functions={}",
                latest.start(), latest.end(), properties.functions());

        for (String functionName : properties.functions()) {
            try {
                collectionService.collectPending(functionName, latest, MAX_WINDOWS_PER_CYCLE);
            } catch (Exception e) {
                log.error("{}: collection failed, will retry next cycle - {}",
                        functionName, e.getMessage());
            }
        }
    }
}