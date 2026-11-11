
package com.cloudops.backend.service;

import com.cloudops.backend.dto.CreateIncidentRequest;
import com.cloudops.backend.dto.IncidentResponse;
import com.cloudops.backend.entity.IncidentEntity;
import com.cloudops.backend.repository.IncidentRepository;
import com.cloudops.backend.enums.IncidentStatus;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public IncidentResponse create(CreateIncidentRequest request) {

        IncidentEntity entity = new IncidentEntity(
                UUID.randomUUID(),
                request.title(),
                request.description(),
                request.severity(),
                "OPEN",
                Instant.now()
        );

        IncidentEntity saved = incidentRepository.save(entity);

        return toResponse(saved);
    }

    public List<IncidentResponse> findAll() {
        return incidentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public IncidentResponse findById(UUID id) {
        IncidentEntity entity = incidentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Incident not found"
                ));

        return toResponse(entity);
    }

    private IncidentResponse toResponse(IncidentEntity entity) {
        return new IncidentResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getSeverity(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }


    @Transactional
    public IncidentResponse updateStatus(
            UUID id,
            IncidentStatus newStatus) {

        IncidentEntity entity = incidentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Incident not found"
                ));

        IncidentStatus currentStatus =
                IncidentStatus.valueOf(entity.getStatus());

        if (currentStatus == IncidentStatus.RESOLVED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Resolved incidents cannot be modified"
            );
        }

        if (currentStatus == IncidentStatus.OPEN
                && newStatus == IncidentStatus.RESOLVED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Incident must be investigated before resolution"
            );
        }

        entity.setStatus(newStatus.name());

        return toResponse(entity);
    }




    public Page<IncidentResponse> findAllPaged(
            int page,
            int size,
            IncidentStatus status,
            String keyword) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        String statusValue =
                status == null ? null : status.name();

        String normalizedKeyword =
                keyword == null || keyword.isBlank()
                        ? null
                        : keyword.trim();

        return incidentRepository.searchIncidents(
                statusValue,
                normalizedKeyword,
                pageable
        ).map(this::toResponse);
    }
}