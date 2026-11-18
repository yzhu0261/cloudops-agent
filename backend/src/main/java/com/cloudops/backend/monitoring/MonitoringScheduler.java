package com.cloudops.backend.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "cloudops.monitoring.enabled", havingValue = "true")
public class MonitoringScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(MonitoringScheduler.class);

    private final MonitoringProperties properties;

    public MonitoringScheduler(MonitoringProperties properties) {
        this.properties = properties;
    }

    @Scheduled(
            initialDelay = 5000,
            fixedDelayString = "${cloudops.monitoring.interval-ms:60000}"
    )
    public void runCycle() {
        log.info("Monitoring cycle started, functions={}", properties.functions());
        for (String functionName : properties.functions()) {
            log.info("Would collect metrics for {}", functionName);
        }
    }
}