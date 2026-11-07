
package com.cloudops.backend.repository;

import com.cloudops.backend.entity.IncidentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface IncidentRepository
        extends JpaRepository<IncidentEntity, UUID> {
}
