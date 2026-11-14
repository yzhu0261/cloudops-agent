
package com.cloudops.backend.repository;

import com.cloudops.backend.entity.IncidentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface IncidentRepository
        extends JpaRepository<IncidentEntity, UUID> {

    Page<IncidentEntity> findByStatus(
            String status,
            Pageable pageable
    );

    @Query("""
        SELECT i
        FROM IncidentEntity i
        WHERE (:status IS NULL OR i.status = :status)
          AND (
              :keyword IS NULL
              OR LOWER(i.title) LIKE LOWER(
                  CONCAT('%', :keyword, '%')
              )
              OR LOWER(i.description) LIKE LOWER(
                  CONCAT('%', :keyword, '%')
              )
          )
        """)
    Page<IncidentEntity> searchIncidents(
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable
    );


}
