
package com.cloudops.backend.controller;

import com.cloudops.backend.dto.CloudWatchMetricResponse;
import com.cloudops.backend.service.CloudWatchMetricService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cloudwatch")
public class CloudWatchMetricController {

    private final CloudWatchMetricService metricService;

    public CloudWatchMetricController(
            CloudWatchMetricService metricService) {
        this.metricService = metricService;
    }

    @GetMapping("/invocations")
    public List<CloudWatchMetricResponse> getInvocations(
            @RequestParam String functionName) {

        return metricService.getInvocations(functionName);
    }
}
