
package com.cloudops.backend.service;

import com.cloudops.backend.dto.CloudWatchMetricResponse;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class CloudWatchMetricService {

    private static final Set<String> SUPPORTED_METRICS =
            Set.of("Invocations", "Errors", "Duration");

    private final CloudWatchClient cloudWatchClient;

    public CloudWatchMetricService(CloudWatchClient cloudWatchClient) {
        this.cloudWatchClient = cloudWatchClient;
    }

    public List<CloudWatchMetricResponse> getInvocations(String functionName) {
        return getMetrics(functionName, "Invocations");
    }

    public List<CloudWatchMetricResponse> getMetrics(
            String functionName,
            String metricName
    ) {

        if (!SUPPORTED_METRICS.contains(metricName)) {
            throw new IllegalArgumentException(
                    "Unsupported metric: " + metricName
            );
        }

        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(24, ChronoUnit.HOURS);

        Statistic statistic = metricName.equals("Duration")
                ? Statistic.AVERAGE
                : Statistic.SUM;

        GetMetricStatisticsRequest request =
                GetMetricStatisticsRequest.builder()
                        .namespace("AWS/Lambda")
                        .metricName(metricName)
                        .dimensions(
                                Dimension.builder()
                                        .name("FunctionName")
                                        .value(functionName)
                                        .build()
                        )
                        .startTime(startTime)
                        .endTime(endTime)
                        .period(3600)
                        .statistics(statistic)
                        .build();

        GetMetricStatisticsResponse response =
                cloudWatchClient.getMetricStatistics(request);

        return response.datapoints()
                .stream()
                .map(datapoint -> new CloudWatchMetricResponse(
                        datapoint.timestamp(),
                        metricName.equals("Duration")
                                ? datapoint.average()
                                : datapoint.sum(),
                        datapoint.unitAsString()
                ))
                .sorted(Comparator.comparing(
                        CloudWatchMetricResponse::timestamp
                ))
                .toList();
    }
}