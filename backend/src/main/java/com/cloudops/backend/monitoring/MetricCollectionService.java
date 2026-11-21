package com.cloudops.backend.monitoring;

import com.cloudops.backend.entity.LambdaMetricSampleEntity;
import com.cloudops.backend.enums.SampleStatus;
import com.cloudops.backend.repository.LambdaMetricSampleRepository;
import com.cloudops.backend.service.CloudWatchMetricService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MetricCollectionService {

    private static final Logger log =
            LoggerFactory.getLogger(MetricCollectionService.class);

    private final CloudWatchMetricService metricService;
    private final LambdaMetricSampleRepository repository;

    public MetricCollectionService(
            CloudWatchMetricService metricService,
            LambdaMetricSampleRepository repository
    ) {
        this.metricService = metricService;
        this.repository = repository;
    }

    public void collect(String functionName, MetricWindow window) {
        if (repository.existsByFunctionNameAndWindowStart(functionName, window.start())) {
            return;
        }

        LambdaWindowMetrics m = metricService.fetchWindow(functionName, window);
        SampleStatus status = m.hasNoData() ? SampleStatus.NO_DATA : SampleStatus.SUCCESS;

        repository.save(new LambdaMetricSampleEntity(
                functionName,
                window.start(),
                window.end(),
                m.invocations(),
                m.errors(),
                m.avgDurationMs(),
                status,
                null
        ));

        log.info("{}: saved {} for window {} ~ {} (invocations={}, errors={}, avgDurationMs={})",
                functionName, status, window.start(), window.end(),
                m.invocations(), m.errors(), m.avgDurationMs());
    }

    public void collectMissing(
            String functionName,
            MetricWindow latest,
            int lookbackWindows,
            int maxWindows
    ) {
        Instant earliest = latest.start()
                .minusSeconds((long) (lookbackWindows - 1) * MetricWindow.PERIOD_SECONDS);

        Set<Instant> existing = repository
                .findByFunctionNameAndWindowStartGreaterThanEqual(functionName, earliest)
                .stream()
                .map(LambdaMetricSampleEntity::getWindowStart)
                .collect(Collectors.toSet());

        List<MetricWindow> missing =
                MetricWindow.missingWindows(latest, lookbackWindows, existing, maxWindows);

        for (MetricWindow window : missing) {
            collect(functionName, window);
        }
    }
}