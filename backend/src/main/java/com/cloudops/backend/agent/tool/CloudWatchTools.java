
package com.cloudops.backend.agent.tool;

import com.cloudops.backend.dto.CloudWatchMetricResponse;
import com.cloudops.backend.service.CloudWatchMetricService;
import com.cloudops.backend.monitoring.LambdaWindowMetrics;
import com.cloudops.backend.monitoring.MetricWindow;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CloudWatchTools {

    private final CloudWatchMetricService metricService;

    public CloudWatchTools(CloudWatchMetricService metricService) {
        this.metricService = metricService;
    }

    @Tool(
            name = "get_lambda_metrics",
            description = "Retrieve real AWS CloudWatch metrics for a Lambda function. " +
                    "Supported metrics: Invocations, Errors, Duration."
    )
    public List<CloudWatchMetricResponse> getLambdaMetrics(
            @ToolParam(description = "AWS Lambda function name")
            String functionName,

            @ToolParam(description = "Invocations, Errors or Duration")
            String metricName
    ) {
        System.out.println(
                "[AGENT TOOL] get_lambda_metrics called: "
                        + functionName + " / " + metricName
        );

        return metricService.getMetrics(functionName, metricName);
    }

    @Tool(
            name = "get_lambda_window_metrics",
            description = "Retrieve real AWS CloudWatch metrics (Invocations, Errors, average Duration in ms) " +
                    "for one specific 5-minute window of a Lambda function. " +
                    "Use this to check what actually happened during an anomaly window."
    )
    public WindowMetricsResult getLambdaWindowMetrics(
            @ToolParam(description = "AWS Lambda function name")
            String functionName,

            @ToolParam(description = "Start of the 5-minute window, UTC ISO-8601, " +
                    "for example 2026-11-18T12:50:00Z. Must fall on a 5-minute boundary.")
            String windowStart
    ) {
        System.out.println(
                "[AGENT TOOL] get_lambda_window_metrics called: "
                        + functionName + " / " + windowStart
        );

        Instant start;
        try {
            start = Instant.parse(windowStart);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "windowStart must be UTC ISO-8601, for example 2026-11-18T12:50:00Z"
            );
        }

        if (start.getEpochSecond() % MetricWindow.PERIOD_SECONDS != 0) {
            throw new IllegalArgumentException(
                    "windowStart must fall on a 5-minute boundary, for example 12:50:00 or 12:55:00"
            );
        }

        MetricWindow window =
                new MetricWindow(start, start.plusSeconds(MetricWindow.PERIOD_SECONDS));
        LambdaWindowMetrics m = metricService.fetchWindow(functionName, window);

        String note = m.hasNoData()
                ? "NO_DATA: CloudWatch returned no data points, usually meaning the function was not invoked in this window"
                : "OK";

        return new WindowMetricsResult(
                functionName, window.start(), window.end(),
                m.invocations(), m.errors(), m.avgDurationMs(), note
        );
    }
}