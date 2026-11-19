package com.cloudops.backend.monitoring;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record MetricWindow(Instant start, Instant end) {

    public static final int PERIOD_SECONDS = 300;
    private static final Duration DELAY = Duration.ofMinutes(2);

    public static MetricWindow latestCompleted(Instant now) {
        Instant delayed = now.minus(DELAY);
        long endSeconds = (delayed.getEpochSecond() / PERIOD_SECONDS) * PERIOD_SECONDS;
        Instant end = Instant.ofEpochSecond(endSeconds);
        return new MetricWindow(end.minusSeconds(PERIOD_SECONDS), end);
    }

    public static List<MetricWindow> pendingWindows(
            Instant lastStoredStart,
            MetricWindow latest,
            int maxWindows
    ) {
        List<MetricWindow> result = new ArrayList<>();

        if (lastStoredStart == null) {
            result.add(latest);
            return result;
        }

        Instant start = lastStoredStart.plusSeconds(PERIOD_SECONDS);
        while (!start.isAfter(latest.start()) && result.size() < maxWindows) {
            result.add(new MetricWindow(start, start.plusSeconds(PERIOD_SECONDS)));
            start = start.plusSeconds(PERIOD_SECONDS);
        }
        return result;
    }
}