package com.cloudops.backend.detection;

import java.util.List;

public class ZScoreDetector {

    private final int minSamples;
    private final double zThreshold;
    private final double minStdDev;

    public ZScoreDetector(int minSamples, double zThreshold, double minStdDev) {
        this.minSamples = minSamples;
        this.zThreshold = zThreshold;
        this.minStdDev = minStdDev;
    }

    public DetectionResult detect(List<Double> history, double current) {
        if (history.size() < minSamples) {
            return new DetectionResult(
                    DetectionStatus.INSUFFICIENT_HISTORY, 0.0, 0.0, 0.0,
                    "Only " + history.size() + " historical samples, need at least " + minSamples
            );
        }

        double mean = history.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        double variance = history.stream()
                .mapToDouble(v -> (v - mean) * (v - mean))
                .average()
                .orElse(0.0);

        double stdDev = Math.sqrt(variance);
        double scale = Math.max(stdDev, minStdDev);
        double z = (current - mean) / scale;

        DetectionStatus status = z >= zThreshold
                ? DetectionStatus.ANOMALY
                : DetectionStatus.NORMAL;

        String reason = String.format(
                "current=%.4f, baseline mean=%.4f, std=%.4f (floor %.4f), z=%.1f, threshold=%.1f",
                current, mean, stdDev, minStdDev, z, zThreshold
        );

        return new DetectionResult(status, z, mean, stdDev, reason);
    }
}