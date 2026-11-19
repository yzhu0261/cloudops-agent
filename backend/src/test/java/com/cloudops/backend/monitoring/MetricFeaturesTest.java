package com.cloudops.backend.monitoring;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MetricFeaturesTest {

    @Test
    void computesRateFromErrorsAndInvocations() {
        assertEquals(0.2, MetricFeatures.errorRate(10.0, 2.0), 1e-9);
    }

    @Test
    void noInvocationsMeansUndefined() {
        assertNull(MetricFeatures.errorRate(null, null));
        assertNull(MetricFeatures.errorRate(0.0, 0.0));
    }

    @Test
    void missingErrorsWithInvocationsMeansZeroRate() {
        assertEquals(0.0, MetricFeatures.errorRate(5.0, null), 1e-9);
    }

    @Test
    void sameErrorCountCanMeanVeryDifferentRates() {
        assertEquals(0.005, MetricFeatures.errorRate(1000.0, 5.0), 1e-9);
        assertEquals(1.0, MetricFeatures.errorRate(5.0, 5.0), 1e-9);
    }
}