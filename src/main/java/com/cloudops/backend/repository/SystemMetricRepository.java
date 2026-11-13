
package com.cloudops.backend.repository;

import com.cloudops.backend.entity.SystemMetricEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SystemMetricRepository
        extends JpaRepository<SystemMetricEntity, UUID> {
}
