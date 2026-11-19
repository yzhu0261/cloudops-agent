package com.cloudops.backend.repository;

import com.cloudops.backend.entity.LambdaMetricSampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;
import java.util.Optional;

public interface LambdaMetricSampleRepository
        extends JpaRepository<LambdaMetricSampleEntity, UUID> {

    boolean existsByFunctionNameAndWindowStart(String functionName, Instant windowStart);

    Optional<LambdaMetricSampleEntity> findTopByFunctionNameOrderByWindowStartDesc(String functionName);
}