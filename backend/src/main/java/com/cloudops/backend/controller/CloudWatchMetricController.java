package com.cloudops.backend.controller;

import com.cloudops.backend.dto.CloudWatchMetricResponse;
import com.cloudops.backend.service.CloudWatchMetricService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cloudwatch")
public class CloudWatchMetricController {

    private final CloudWatchMetricService cloudWatchMetricService;

    public CloudWatchMetricController(
            CloudWatchMetricService cloudWatchMetricService
    ) {
        this.cloudWatchMetricService = cloudWatchMetricService;
    }

    @GetMapping("/invocations")
    public List<CloudWatchMetricResponse> getInvocations(
            @RequestParam String functionName
    ) {
        return cloudWatchMetricService.getInvocations(functionName);
    }

    @GetMapping("/metrics")
    public List<CloudWatchMetricResponse> getMetrics(
            @RequestParam String functionName,
            @RequestParam String metricName
    ) {
        return cloudWatchMetricService.getMetrics(functionName, metricName);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidMetric(IllegalArgumentException ex) {
        return ex.getMessage();
    }
}