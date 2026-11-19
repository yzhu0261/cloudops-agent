package com.cloudops.backend.repository;

import com.cloudops.backend.entity.LambdaMetricSampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;
import java.util.Optional;
import java.util.List;

public interface LambdaMetricSampleRepository
        extends JpaRepository<LambdaMetricSampleEntity, UUID> {

    boolean existsByFunctionNameAndWindowStart(String functionName, Instant windowStart);

    Optional<LambdaMetricSampleEntity> findTopByFunctionNameOrderByWindowStartDesc(String functionName);

    List<LambdaMetricSampleEntity> findByFunctionNameAndWindowStartGreaterThanEqual(
            String functionName, Instant from);
}