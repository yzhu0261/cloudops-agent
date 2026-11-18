package com.cloudops.backend.monitoring;

import java.time.Duration;
import java.time.Instant;

public record MetricWindow(Instant start, Instant end) {

    public static final int PERIOD_SECONDS = 300;
    private static final Duration DELAY = Duration.ofMinutes(2);

    public static MetricWindow latestCompleted(Instant now) {
        Instant delayed = now.minus(DELAY);
        long endSeconds = (delayed.getEpochSecond() / PERIOD_SECONDS) * PERIOD_SECONDS;
        Instant end = Instant.ofEpochSecond(endSeconds);
        return new MetricWindow(end.minusSeconds(PERIOD_SECONDS), end);
    }
}