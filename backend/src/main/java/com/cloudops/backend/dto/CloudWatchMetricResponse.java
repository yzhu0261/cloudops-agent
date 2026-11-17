package com.cloudops.backend.dto;

import java.time.Instant;

public record CloudWatchMetricResponse(
        Instant timestamp,
        Double value,
        String unit
) {}