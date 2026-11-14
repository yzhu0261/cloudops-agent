package com.cloudops.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record IncidentResponse(
        UUID id,
        String title,
        String description,
        String severity,
        String status,
        Instant createdAt
) {}