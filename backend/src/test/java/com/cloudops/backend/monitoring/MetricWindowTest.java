package com.cloudops.backend.monitoring;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MetricWindowTest {

    @Test
    void picksLatestCompletedWindow() {
        MetricWindow w = MetricWindow.latestCompleted(Instant.parse("2026-11-19T12:57:52Z"));

        assertEquals(Instant.parse("2026-11-19T12:50:00Z"), w.start());
        assertEquals(Instant.parse("2026-11-19T12:55:00Z"), w.end());
    }

    @Test
    void waitsForDelayBeforeMovingToNextWindow() {
        MetricWindow w = MetricWindow.latestCompleted(Instant.parse("2026-11-19T12:55:30Z"));

        assertEquals(Instant.parse("2026-11-19T12:45:00Z"), w.start());
        assertEquals(Instant.parse("2026-11-19T12:50:00Z"), w.end());
    }

    @Test
    void movesToNextWindowExactlyAfterDelay() {
        MetricWindow w = MetricWindow.latestCompleted(Instant.parse("2026-11-19T12:57:00Z"));

        assertEquals(Instant.parse("2026-11-19T12:50:00Z"), w.start());
        assertEquals(Instant.parse("2026-11-19T12:55:00Z"), w.end());
    }
}