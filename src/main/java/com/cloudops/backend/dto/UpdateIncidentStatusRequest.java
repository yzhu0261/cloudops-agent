
package com.cloudops.backend.dto;

import com.cloudops.backend.enums.IncidentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateIncidentStatusRequest(
        @NotNull IncidentStatus status
) {}
