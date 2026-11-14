
package com.cloudops.backend.repository;

import com.cloudops.backend.entity.SystemMetricEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SystemMetricRepository
        extends JpaRepository<SystemMetricEntity, UUID> {

    List<SystemMetricEntity> findAllByOrderByCollectedAtDesc(
            Pageable pageable
    );
}