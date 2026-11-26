package com.cloudops.backend.entity;

import com.cloudops.backend.enums.AnomalyMetric;
import com.cloudops.backend.enums.Severity;
import com.cloudops.backend.enums.InvestigationStatus;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "anomaly_events")
public class AnomalyEventEntity {

    @Id
    private UUID id;

    @Column(name = "function_name", nullable = false)
    private String functionName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnomalyMetric metric;

    @Column(name = "window_start", nullable = false)
    private Instant windowStart;

    @Column(name = "window_end", nullable = false)
    private Instant windowEnd;

    @Column(name = "observed_value", nullable = false)
    private double observedValue;

    @Column(name = "baseline_mean", nullable = false)
    private double baselineMean;

    @Column(name = "baseline_std", nullable = false)
    private double baselineStd;

    @Column(name = "z_score", nullable = false)
    private double zScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "detected_at", nullable = false)
    private Instant detectedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "investigation_status", nullable = false)
    private InvestigationStatus investigationStatus;

    @Column(name = "investigation_report", columnDefinition = "TEXT")
    private String investigationReport;

    @Column(name = "investigation_error", columnDefinition = "TEXT")
    private String investigationError;

    @Column(name = "investigation_finished_at")
    private Instant investigationFinishedAt;

    protected AnomalyEventEntity() {
    }

    public AnomalyEventEntity(
            String functionName,
            AnomalyMetric metric,
            Instant windowStart,
            Instant windowEnd,
            double observedValue,
            double baselineMean,
            double baselineStd,
            double zScore,
            Severity severity,
            String reason
    ) {
        this.id = UUID.randomUUID();
        this.functionName = functionName;
        this.metric = metric;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.observedValue = observedValue;
        this.baselineMean = baselineMean;
        this.baselineStd = baselineStd;
        this.zScore = zScore;
        this.severity = severity;
        this.reason = reason;
        this.detectedAt = Instant.now();
        this.investigationStatus = InvestigationStatus.PENDING;
    }

    public UUID getId() { return id; }
    public String getFunctionName() { return functionName; }
    public AnomalyMetric getMetric() { return metric; }
    public Instant getWindowStart() { return windowStart; }
    public Instant getWindowEnd() { return windowEnd; }
    public double getObservedValue() { return observedValue; }
    public double getBaselineMean() { return baselineMean; }
    public double getBaselineStd() { return baselineStd; }
    public double getZScore() { return zScore; }
    public Severity getSeverity() { return severity; }
    public String getReason() { return reason; }
    public Instant getDetectedAt() { return detectedAt; }
    public InvestigationStatus getInvestigationStatus() { return investigationStatus; }
    public String getInvestigationReport() { return investigationReport; }
    public String getInvestigationError() { return investigationError; }
    public Instant getInvestigationFinishedAt() { return investigationFinishedAt; }
    public void setInvestigationStatus(InvestigationStatus status) { this.investigationStatus = status; };

    public void markCompleted(String report) {
        this.investigationStatus = InvestigationStatus.COMPLETED;
        this.investigationReport = report;
        this.investigationError = null;
        this.investigationFinishedAt = Instant.now();
    }

    public void markFailed(String error) {
        this.investigationStatus = InvestigationStatus.FAILED;
        this.investigationError = error;
        this.investigationReport = null;
        this.investigationFinishedAt = Instant.now();
    }
}