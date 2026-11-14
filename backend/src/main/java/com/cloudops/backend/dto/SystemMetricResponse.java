
package com.cloudops.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record SystemMetricResponse(
        UUID id,
        Double cpuUsage,
        Double memoryUsage,
        Instant collectedAt
) {
}
