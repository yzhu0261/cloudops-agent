
package com.cloudops.backend.service;

import com.cloudops.backend.entity.SystemMetricEntity;
import com.cloudops.backend.repository.SystemMetricRepository;
import com.sun.management.OperatingSystemMXBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.UUID;

@Service
public class MonitoringService {

    private final SystemMetricRepository metricRepository;

    private final OperatingSystemMXBean osBean =
            ManagementFactory.getPlatformMXBean(
                    OperatingSystemMXBean.class
            );

    public MonitoringService(
            SystemMetricRepository metricRepository
    ) {
        this.metricRepository = metricRepository;
    }

    @Scheduled(fixedRate = 10000)
    public void collectMetrics() {

        double cpuUsage = osBean.getCpuLoad() * 100;

        long totalMemory = osBean.getTotalMemorySize();
        long freeMemory = osBean.getFreeMemorySize();

        if (cpuUsage < 0 || totalMemory <= 0
                || freeMemory < 0 || freeMemory > totalMemory) {
            System.out.println(
                    "Metrics unavailable. Skipping collection."
            );
            return;
        }

        double memoryUsage =
                (double) (totalMemory - freeMemory)
                        / totalMemory * 100;

        SystemMetricEntity metric = new SystemMetricEntity(
                UUID.randomUUID(),
                cpuUsage,
                memoryUsage,
                Instant.now()
        );

        metricRepository.save(metric);

        System.out.printf(
                "Metrics saved: CPU=%.2f%%, Memory=%.2f%%%n",
                cpuUsage,
                memoryUsage
        );
    }
}
