
package com.cloudops.backend.service;

import com.cloudops.backend.dto.SystemMetricResponse;
import com.cloudops.backend.entity.SystemMetricEntity;
import com.cloudops.backend.repository.SystemMetricRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemMetricService {

    private final SystemMetricRepository metricRepository;

    public SystemMetricService(
            SystemMetricRepository metricRepository
    ) {
        this.metricRepository = metricRepository;
    }

    public List<SystemMetricResponse> getLatestMetrics(int limit) {

        List<SystemMetricEntity> metrics =
                metricRepository.findAllByOrderByCollectedAtDesc(
                        PageRequest.of(0, limit)
                );

        return metrics.stream()
                .map(metric -> new SystemMetricResponse(
                        metric.getId(),
                        metric.getCpuUsage(),
                        metric.getMemoryUsage(),
                        metric.getCollectedAt()
                ))
                .toList();
    }
}
