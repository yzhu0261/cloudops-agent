
package com.cloudops.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateIncidentRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String severity
) {}
