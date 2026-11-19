package com.cloudops.backend.monitoring;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import java.util.Set;

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

    private static MetricWindow w(String start, String end) {
        return new MetricWindow(Instant.parse(start), Instant.parse(end));
    }

    @Test
    void noHistoryReturnsOnlyLatestWindow() {
        MetricWindow latest = w("2026-11-19T12:55:00Z", "2026-11-19T13:00:00Z");

        List<MetricWindow> result = MetricWindow.pendingWindows(null, latest, 12);

        assertEquals(List.of(latest), result);
    }

    @Test
    void upToDateReturnsNothing() {
        MetricWindow latest = w("2026-11-19T12:55:00Z", "2026-11-19T13:00:00Z");

        List<MetricWindow> result = MetricWindow.pendingWindows(
                Instant.parse("2026-11-19T12:55:00Z"), latest, 12);

        assertEquals(List.of(), result);
    }

    @Test
    void fillsGapOldestFirst() {
        MetricWindow latest = w("2026-11-19T12:55:00Z", "2026-11-19T13:00:00Z");

        List<MetricWindow> result = MetricWindow.pendingWindows(
                Instant.parse("2026-11-19T12:40:00Z"), latest, 12);

        assertEquals(List.of(
                w("2026-11-19T12:45:00Z", "2026-11-19T12:50:00Z"),
                w("2026-11-19T12:50:00Z", "2026-11-19T12:55:00Z"),
                w("2026-11-19T12:55:00Z", "2026-11-19T13:00:00Z")
        ), result);
    }

    @Test
    void capsNumberOfWindowsPerCycle() {
        MetricWindow latest = w("2026-11-19T12:55:00Z", "2026-11-19T13:00:00Z");

        List<MetricWindow> result = MetricWindow.pendingWindows(
                Instant.parse("2026-11-19T12:25:00Z"), latest, 2);

        assertEquals(List.of(
                w("2026-11-19T12:30:00Z", "2026-11-19T12:35:00Z"),
                w("2026-11-19T12:35:00Z", "2026-11-19T12:40:00Z")
        ), result);
    }

    @Test
    void missingWindowsWithNothingStoredReturnsAllOldestFirst() {
        MetricWindow latest = w("2026-11-19T12:55:00Z", "2026-11-19T13:00:00Z");

        List<MetricWindow> result = MetricWindow.missingWindows(latest, 3, Set.of(), 12);

        assertEquals(List.of(
                w("2026-11-19T12:45:00Z", "2026-11-19T12:50:00Z"),
                w("2026-11-19T12:50:00Z", "2026-11-19T12:55:00Z"),
                w("2026-11-19T12:55:00Z", "2026-11-19T13:00:00Z")
        ), result);
    }

    @Test
    void missingWindowsFindsGapInTheMiddle() {
        MetricWindow latest = w("2026-11-19T12:55:00Z", "2026-11-19T13:00:00Z");
        Set<Instant> existing = Set.of(
                Instant.parse("2026-11-19T12:40:00Z"),
                Instant.parse("2026-11-19T12:55:00Z")
        );

        List<MetricWindow> result = MetricWindow.missingWindows(latest, 4, existing, 12);

        assertEquals(List.of(
                w("2026-11-19T12:45:00Z", "2026-11-19T12:50:00Z"),
                w("2026-11-19T12:50:00Z", "2026-11-19T12:55:00Z")
        ), result);
    }

    @Test
    void missingWindowsReturnsNothingWhenComplete() {
        MetricWindow latest = w("2026-11-19T12:55:00Z", "2026-11-19T13:00:00Z");
        Set<Instant> existing = Set.of(
                Instant.parse("2026-11-19T12:50:00Z"),
                Instant.parse("2026-11-19T12:55:00Z")
        );

        List<MetricWindow> result = MetricWindow.missingWindows(latest, 2, existing, 12);

        assertEquals(List.of(), result);
    }

    @Test
    void missingWindowsRespectsMaxPerCycle() {
        MetricWindow latest = w("2026-11-19T12:55:00Z", "2026-11-19T13:00:00Z");

        List<MetricWindow> result = MetricWindow.missingWindows(latest, 4, Set.of(), 2);

        assertEquals(List.of(
                w("2026-11-19T12:40:00Z", "2026-11-19T12:45:00Z"),
                w("2026-11-19T12:45:00Z", "2026-11-19T12:50:00Z")
        ), result);
    }
}