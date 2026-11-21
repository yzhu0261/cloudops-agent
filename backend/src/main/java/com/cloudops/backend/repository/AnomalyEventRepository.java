package com.cloudops.backend.repository;

import com.cloudops.backend.entity.AnomalyEventEntity;
import com.cloudops.backend.enums.AnomalyMetric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface AnomalyEventRepository extends JpaRepository<AnomalyEventEntity, UUID> {

    boolean existsByFunctionNameAndMetricAndWindowStart(
            String functionName, AnomalyMetric metric, Instant windowStart);
}