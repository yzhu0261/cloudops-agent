package com.cloudops.backend.detection;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZScoreDetectorTest {

    private final ZScoreDetector detector = new ZScoreDetector(10, 3.0, 0.01);

    private static final List<Double> NOISY_HISTORY =
            List.of(0.0, 0.1, 0.0, 0.1, 0.0, 0.1, 0.0, 0.1, 0.0, 0.1);

    @Test
    void tooLittleHistoryGivesNoVerdict() {
        DetectionResult r = detector.detect(List.of(0.0, 0.0, 0.0), 0.9);

        assertEquals(DetectionStatus.INSUFFICIENT_HISTORY, r.status());
    }

    @Test
    void unchangedValueIsNormal() {
        DetectionResult r = detector.detect(Collections.nCopies(20, 0.0), 0.0);

        assertEquals(DetectionStatus.NORMAL, r.status());
    }

    @Test
    void errorSpikeAfterCleanHistoryIsAnomaly() {
        DetectionResult r = detector.detect(Collections.nCopies(20, 0.0), 0.5);

        assertEquals(DetectionStatus.ANOMALY, r.status());
    }

    @Test
    void tinyDeviationBelowFloorIsNotAnomaly() {
        DetectionResult r = detector.detect(Collections.nCopies(20, 0.0), 0.02);

        assertEquals(DetectionStatus.NORMAL, r.status());
    }

    @Test
    void improvementIsNeverAnomaly() {
        DetectionResult r = detector.detect(NOISY_HISTORY, 0.0);

        assertEquals(DetectionStatus.NORMAL, r.status());
    }

    @Test
    void noisyHistoryToleratesModerateValueButFlagsLargeOne() {
        assertEquals(DetectionStatus.NORMAL, detector.detect(NOISY_HISTORY, 0.12).status());
        assertEquals(DetectionStatus.ANOMALY, detector.detect(NOISY_HISTORY, 0.25).status());
    }
}