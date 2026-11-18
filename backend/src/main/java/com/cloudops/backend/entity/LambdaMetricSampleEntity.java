package com.cloudops.backend.entity;

import com.cloudops.backend.enums.SampleStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "lambda_metric_samples")
public class LambdaMetricSampleEntity {

    @Id
    private UUID id;

    @Column(name = "function_name", nullable = false)
    private String functionName;

    @Column(name = "window_start", nullable = false)
    private Instant windowStart;

    @Column(name = "window_end", nullable = false)
    private Instant windowEnd;

    private Double invocations;

    private Double errors;

    @Column(name = "avg_duration_ms")
    private Double avgDurationMs;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SampleStatus status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "collected_at", nullable = false)
    private Instant collectedAt;

    protected LambdaMetricSampleEntity() {
    }

    public LambdaMetricSampleEntity(
            String functionName,
            Instant windowStart,
            Instant windowEnd,
            Double invocations,
            Double errors,
            Double avgDurationMs,
            SampleStatus status,
            String errorMessage
    ) {
        this.id = UUID.randomUUID();
        this.functionName = functionName;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.invocations = invocations;
        this.errors = errors;
        this.avgDurationMs = avgDurationMs;
        this.status = status;
        this.errorMessage = errorMessage;
        this.collectedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getFunctionName() { return functionName; }
    public Instant getWindowStart() { return windowStart; }
    public Instant getWindowEnd() { return windowEnd; }
    public Double getInvocations() { return invocations; }
    public Double getErrors() { return errors; }
    public Double getAvgDurationMs() { return avgDurationMs; }
    public SampleStatus getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getCollectedAt() { return collectedAt; }
}