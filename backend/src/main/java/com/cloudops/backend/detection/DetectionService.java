package com.cloudops.backend.detection;

import com.cloudops.backend.entity.LambdaMetricSampleEntity;
import com.cloudops.backend.monitoring.MetricFeatures;
import com.cloudops.backend.monitoring.MetricWindow;
import com.cloudops.backend.repository.LambdaMetricSampleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DetectionService {

    private static final Logger log = LoggerFactory.getLogger(DetectionService.class);

    private static final int HISTORY_WINDOWS = 864;

    private final LambdaMetricSampleRepository repository;
    private final ZScoreDetector errorRateDetector = new ZScoreDetector(10, 3.0, 0.01);
    private final Map<String, Instant> lastEvaluated = new ConcurrentHashMap<>();

    public DetectionService(LambdaMetricSampleRepository repository) {
        this.repository = repository;
    }

    public void evaluate(String functionName, MetricWindow window) {
        if (window.start().equals(lastEvaluated.get(functionName))) {
            return;
        }

        Instant earliest = window.start()
                .minusSeconds((long) HISTORY_WINDOWS * MetricWindow.PERIOD_SECONDS);

        List<LambdaMetricSampleEntity> samples = repository
                .findByFunctionNameAndWindowStartGreaterThanEqual(functionName, earliest);

        boolean currentFound = false;
        Double currentRate = null;
        List<Double> history = new ArrayList<>();

        for (LambdaMetricSampleEntity sample : samples) {
            Double rate = MetricFeatures.errorRate(sample.getInvocations(), sample.getErrors());

            if (sample.getWindowStart().equals(window.start())) {
                currentFound = true;
                currentRate = rate;
            } else if (sample.getWindowStart().isBefore(window.start()) && rate != null) {
                history.add(rate);
            }
        }

        if (!currentFound) {
            return;
        }

        lastEvaluated.put(functionName, window.start());

        if (currentRate == null) {
            log.info("{}: window {} has no invocations, nothing to evaluate",
                    functionName, window.start());
            return;
        }

        DetectionResult result = errorRateDetector.detect(history, currentRate);

        if (result.status() == DetectionStatus.ANOMALY) {
            log.warn("{}: error-rate ANOMALY in window {} ({})",
                    functionName, window.start(), result.reason());
        } else {
            log.info("{}: error-rate {} in window {} ({})",
                    functionName, result.status(), window.start(), result.reason());
        }
    }
}