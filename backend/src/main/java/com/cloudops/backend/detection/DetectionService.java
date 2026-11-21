package com.cloudops.backend.detection;

import com.cloudops.backend.entity.LambdaMetricSampleEntity;
import com.cloudops.backend.monitoring.MetricFeatures;
import com.cloudops.backend.monitoring.MetricWindow;
import com.cloudops.backend.repository.LambdaMetricSampleRepository;
import com.cloudops.backend.entity.AnomalyEventEntity;
import com.cloudops.backend.enums.AnomalyMetric;
import com.cloudops.backend.enums.Severity;
import com.cloudops.backend.repository.AnomalyEventRepository;

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
    private static final double CRITICAL_Z = 6.0;

    private final LambdaMetricSampleRepository repository;
    private final AnomalyEventRepository anomalyRepository;
    private final ZScoreDetector errorRateDetector = new ZScoreDetector(10, 3.0, 0.01);
    private final Map<String, Instant> lastEvaluated = new ConcurrentHashMap<>();


    public DetectionService(LambdaMetricSampleRepository repository, AnomalyEventRepository anomalyRepository) {
        this.repository = repository;
        this.anomalyRepository = anomalyRepository;
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

        if (currentRate == null) {
            log.info("{}: window {} has no invocations, nothing to evaluate",
                    functionName, window.start());
            lastEvaluated.put(functionName, window.start());
            return;
        }

        DetectionResult result = errorRateDetector.detect(history, currentRate);

        if (result.status() == DetectionStatus.ANOMALY) {
            log.warn("{}: error-rate ANOMALY in window {} ({})",
                    functionName, window.start(), result.reason());
            recordAnomaly(functionName, window, currentRate, result);
        } else {
            log.info("{}: error-rate {} in window {} ({})",
                    functionName, result.status(), window.start(), result.reason());
        }
        lastEvaluated.put(functionName, window.start());

    }

    private void recordAnomaly(
            String functionName,
            MetricWindow window,
            double observed,
            DetectionResult result
    ) {
        if (anomalyRepository.existsByFunctionNameAndMetricAndWindowStart(
                functionName, AnomalyMetric.ERROR_RATE, window.start())) {
            return;
        }

        Severity severity = result.zScore() >= CRITICAL_Z
                ? Severity.CRITICAL
                : Severity.WARNING;

        anomalyRepository.save(new AnomalyEventEntity(
                functionName,
                AnomalyMetric.ERROR_RATE,
                window.start(),
                window.end(),
                observed,
                result.mean(),
                result.stdDev(),
                result.zScore(),
                severity,
                result.reason()
        ));

        log.warn("{}: anomaly event saved (severity={}) for window {}",
                functionName, severity, window.start());
    }
}