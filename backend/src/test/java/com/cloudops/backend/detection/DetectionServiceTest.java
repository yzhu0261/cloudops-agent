package com.cloudops.backend.detection;

import com.cloudops.backend.entity.AnomalyEventEntity;
import com.cloudops.backend.entity.LambdaMetricSampleEntity;
import com.cloudops.backend.enums.AnomalyMetric;
import com.cloudops.backend.enums.SampleStatus;
import com.cloudops.backend.enums.Severity;
import com.cloudops.backend.monitoring.MetricWindow;
import com.cloudops.backend.repository.AnomalyEventRepository;
import com.cloudops.backend.repository.LambdaMetricSampleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DetectionServiceTest {

    private static final String FN = "SaveWorkout";
    private static final Instant CURRENT_START = Instant.parse("2026-11-20T12:00:00Z");

    private final MetricWindow window =
            new MetricWindow(CURRENT_START, CURRENT_START.plusSeconds(300));

    private LambdaMetricSampleRepository sampleRepo;
    private AnomalyEventRepository anomalyRepo;
    private DetectionService service;

    @BeforeEach
    void setUp() {
        sampleRepo = mock(LambdaMetricSampleRepository.class);
        anomalyRepo = mock(AnomalyEventRepository.class);
        service = new DetectionService(sampleRepo, anomalyRepo);
    }

    private LambdaMetricSampleEntity sample(Instant start, double invocations, double errors) {
        return new LambdaMetricSampleEntity(
                FN, start, start.plusSeconds(300),
                invocations, errors, 100.0, SampleStatus.SUCCESS, null);
    }

    private void givenSamples(int historyCount, double currentErrors) {
        List<LambdaMetricSampleEntity> list = new ArrayList<>();
        for (int i = 1; i <= historyCount; i++) {
            list.add(sample(CURRENT_START.minusSeconds(300L * i), 10, 0));
        }
        list.add(sample(CURRENT_START, 10, currentErrors));

        when(sampleRepo.findByFunctionNameAndWindowStartGreaterThanEqual(eq(FN), any()))
                .thenReturn(list);
    }

    @Test
    void errorSpikeIsSavedAsCriticalEvent() {
        givenSamples(15, 5);

        service.evaluate(FN, window);

        ArgumentCaptor<AnomalyEventEntity> captor =
                ArgumentCaptor.forClass(AnomalyEventEntity.class);
        verify(anomalyRepo, times(1)).save(captor.capture());

        AnomalyEventEntity saved = captor.getValue();
        assertEquals(FN, saved.getFunctionName());
        assertEquals(AnomalyMetric.ERROR_RATE, saved.getMetric());
        assertEquals(CURRENT_START, saved.getWindowStart());
        assertEquals(0.5, saved.getObservedValue(), 1e-9);
        assertEquals(Severity.CRITICAL, saved.getSeverity());
    }

    @Test
    void normalWindowSavesNothing() {
        givenSamples(15, 0);

        service.evaluate(FN, window);

        verify(anomalyRepo, never()).save(any(AnomalyEventEntity.class));
    }

    @Test
    void notEnoughHistorySavesNothing() {
        givenSamples(3, 5);

        service.evaluate(FN, window);

        verify(anomalyRepo, never()).save(any(AnomalyEventEntity.class));
    }

    @Test
    void sameWindowEvaluatedTwiceSavesOnlyOnce() {
        givenSamples(15, 5);

        service.evaluate(FN, window);
        service.evaluate(FN, window);

        verify(anomalyRepo, times(1)).save(any(AnomalyEventEntity.class));
    }

    @Test
    void failedSaveIsRetriedOnNextCycle() {
        givenSamples(15, 5);
        when(anomalyRepo.save(any(AnomalyEventEntity.class)))
                .thenThrow(new RuntimeException("db down"))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(RuntimeException.class, () -> service.evaluate(FN, window));
        service.evaluate(FN, window);

        verify(anomalyRepo, times(2)).save(any(AnomalyEventEntity.class));
    }
}