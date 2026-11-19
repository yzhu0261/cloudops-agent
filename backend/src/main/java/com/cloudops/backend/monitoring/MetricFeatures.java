package com.cloudops.backend.monitoring;

public final class MetricFeatures {

    private MetricFeatures() {
    }

    public static Double errorRate(Double invocations, Double errors) {
        if (invocations == null || invocations <= 0) {
            return null;
        }
        double errorCount = (errors == null) ? 0.0 : errors;
        return errorCount / invocations;
    }
}