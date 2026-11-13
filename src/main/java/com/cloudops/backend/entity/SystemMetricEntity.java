
package com.cloudops.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "system_metrics")
public class SystemMetricEntity {

    @Id
    private UUID id;

    @Column(name = "cpu_usage")
    private Double cpuUsage;

    @Column(name = "memory_usage")
    private Double memoryUsage;

    @Column(name = "collected_at")
    private Instant collectedAt;

    protected SystemMetricEntity() {
    }

    public SystemMetricEntity(
            UUID id,
            Double cpuUsage,
            Double memoryUsage,
            Instant collectedAt
    ) {
        this.id = id;
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.collectedAt = collectedAt;
    }

    public UUID getId() {
        return id;
    }

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public Double getMemoryUsage() {
        return memoryUsage;
    }

    public Instant getCollectedAt() {
        return collectedAt;
    }
}
