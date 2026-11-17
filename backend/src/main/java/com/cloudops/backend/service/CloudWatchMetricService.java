
package com.cloudops.backend.service;

import com.cloudops.backend.dto.CloudWatchMetricResponse;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
public class CloudWatchMetricService {

    private final CloudWatchClient cloudWatchClient;

    public CloudWatchMetricService(CloudWatchClient cloudWatchClient) {
        this.cloudWatchClient = cloudWatchClient;
    }

    public List<CloudWatchMetricResponse> getInvocations(String functionName) {

        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(24, ChronoUnit.HOURS);

        GetMetricStatisticsRequest request =
                GetMetricStatisticsRequest.builder()
                        .namespace("AWS/Lambda")
                        .metricName("Invocations")
                        .dimensions(
                                Dimension.builder()
                                        .name("FunctionName")
                                        .value(functionName)
                                        .build()
                        )
                        .startTime(startTime)
                        .endTime(endTime)
                        .period(3600)
                        .statistics(Statistic.SUM)
                        .build();

        GetMetricStatisticsResponse response =
                cloudWatchClient.getMetricStatistics(request);

        return response.datapoints()
                .stream()
                .map(datapoint -> new CloudWatchMetricResponse(
                        datapoint.timestamp(),
                        datapoint.sum(),
                        datapoint.unitAsString()
                ))
                .sorted(Comparator.comparing(
                        CloudWatchMetricResponse::timestamp
                ))
                .toList();
    }
}
