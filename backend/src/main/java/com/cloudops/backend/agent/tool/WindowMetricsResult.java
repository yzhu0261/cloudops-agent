package com.cloudops.backend.agent.tool;

import java.time.Instant;

public record WindowMetricsResult(
        String functionName,
        Instant windowStart,
        Instant windowEnd,
        Double invocations,
        Double errors,
        Double avgDurationMs,
        String note
) {
}