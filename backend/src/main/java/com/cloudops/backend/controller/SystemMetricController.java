
package com.cloudops.backend.controller;

import com.cloudops.backend.dto.SystemMetricResponse;
import com.cloudops.backend.service.SystemMetricService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metrics")
public class SystemMetricController {

    private final SystemMetricService metricService;

    public SystemMetricController(
            SystemMetricService metricService
    ) {
        this.metricService = metricService;
    }

    @GetMapping
    public List<SystemMetricResponse> getMetrics(
            @RequestParam(defaultValue = "50") int limit
    ) {
        if (limit < 1 || limit > 500) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "limit must be between 1 and 500"
            );
        }

        return metricService.getLatestMetrics(limit);
    }
}
