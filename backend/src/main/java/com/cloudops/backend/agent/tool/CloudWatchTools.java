
package com.cloudops.backend.agent.tool;

import com.cloudops.backend.dto.CloudWatchMetricResponse;
import com.cloudops.backend.service.CloudWatchMetricService;
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
}