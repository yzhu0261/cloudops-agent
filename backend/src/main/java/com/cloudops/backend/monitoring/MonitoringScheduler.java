package com.cloudops.backend.monitoring;

import com.cloudops.backend.detection.DetectionService;
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

    private static final int MAX_WINDOWS_PER_CYCLE = 12;
    private static final int LOOKBACK_WINDOWS = 864;

    private final MonitoringProperties properties;
    private final MetricCollectionService collectionService;
    private final DetectionService detectionService;

    public MonitoringScheduler(
            MonitoringProperties properties,
            MetricCollectionService collectionService,
            DetectionService detectionService
    ) {
        this.properties = properties;
        this.collectionService = collectionService;
        this.detectionService = detectionService;
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
                collectionService.collectMissing(
                        functionName, latest, LOOKBACK_WINDOWS, MAX_WINDOWS_PER_CYCLE);
                detectionService.evaluate(functionName, latest);
            } catch (Exception e) {
                log.error("{}: monitoring cycle failed, will retry next cycle - {}",
                        functionName, e.getMessage());
            }
        }
    }
}