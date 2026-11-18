package com.cloudops.backend.monitoring;

import com.cloudops.backend.service.CloudWatchMetricService;
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
    private final CloudWatchMetricService metricService;

    public MonitoringScheduler(
            MonitoringProperties properties,
            CloudWatchMetricService metricService
    ) {
        this.properties = properties;
        this.metricService = metricService;
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
                LambdaWindowMetrics m = metricService.fetchWindow(functionName, window);
                if (m.hasNoData()) {
                    log.info("{}: NO_DATA in this window", functionName);
                } else {
                    log.info("{}: invocations={}, errors={}, avgDurationMs={}",
                            functionName, m.invocations(), m.errors(), m.avgDurationMs());
                }
            } catch (Exception e) {
                log.error("{}: AWS_ERROR - {}", functionName, e.getMessage());
            }
        }
    }
}