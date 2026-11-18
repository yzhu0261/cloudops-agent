package com.cloudops.backend.monitoring;

public record LambdaWindowMetrics(
        Double invocations,
        Double errors,
        Double avgDurationMs
) {
    public boolean hasNoData() {
        return invocations == null && errors == null && avgDurationMs == null;
    }
}