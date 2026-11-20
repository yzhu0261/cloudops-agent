package com.cloudops.backend.detection;

public record DetectionResult(
        DetectionStatus status,
        double zScore,
        double mean,
        double stdDev,
        String reason
) {
}