
package com.cloudops.backend;

import com.cloudops.backend.dto.CreateIncidentRequest;
import com.cloudops.backend.dto.IncidentResponse;
import com.cloudops.backend.entity.IncidentEntity;
import com.cloudops.backend.enums.IncidentStatus;
import com.cloudops.backend.repository.IncidentRepository;
import com.cloudops.backend.service.IncidentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IncidentServiceTest {

    private IncidentRepository incidentRepository;
    private IncidentService incidentService;

    @BeforeEach
    void setUp() {
        incidentRepository = mock(IncidentRepository.class);
        incidentService = new IncidentService(incidentRepository);
    }

    @Test
    void shouldCreateIncidentWithOpenStatus() {

        CreateIncidentRequest request =
                new CreateIncidentRequest(
                        "AWS Lambda Timeout",
                        "Lambda execution exceeded timeout",
                        "HIGH"
                );

        when(incidentRepository.save(any(IncidentEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        IncidentResponse response = incidentService.create(request);

        assertEquals("AWS Lambda Timeout", response.title());
        assertEquals("HIGH", response.severity());
        assertEquals("OPEN", response.status());
        assertNotNull(response.id());
        assertNotNull(response.createdAt());

        verify(incidentRepository).save(any(IncidentEntity.class));
    }

    @Test
    void shouldReturn404WhenIncidentNotFound() {

        UUID id = UUID.randomUUID();

        when(incidentRepository.findById(id))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> incidentService.findById(id)
                );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void shouldUpdateOpenIncidentToInvestigating() {

        UUID id = UUID.randomUUID();

        IncidentEntity entity = new IncidentEntity(
                id,
                "Database Connection Failure",
                "PostgreSQL connection failed",
                "HIGH",
                "OPEN",
                Instant.now()
        );

        when(incidentRepository.findById(id))
                .thenReturn(Optional.of(entity));

        IncidentResponse response = incidentService.updateStatus(
                id,
                IncidentStatus.INVESTIGATING
        );

        assertEquals("INVESTIGATING", response.status());
        assertEquals("INVESTIGATING", entity.getStatus());
    }

    @Test
    void shouldRejectDirectResolutionFromOpen() {

        UUID id = UUID.randomUUID();

        IncidentEntity entity = new IncidentEntity(
                id,
                "AWS Lambda Timeout",
                "Lambda timed out",
                "HIGH",
                "OPEN",
                Instant.now()
        );

        when(incidentRepository.findById(id))
                .thenReturn(Optional.of(entity));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> incidentService.updateStatus(
                                id,
                                IncidentStatus.RESOLVED
                        )
                );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("OPEN", entity.getStatus());
    }

    @Test
    void shouldRejectChangesToResolvedIncident() {

        UUID id = UUID.randomUUID();

        IncidentEntity entity = new IncidentEntity(
                id,
                "Resolved Incident",
                "Issue already resolved",
                "LOW",
                "RESOLVED",
                Instant.now()
        );

        when(incidentRepository.findById(id))
                .thenReturn(Optional.of(entity));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> incidentService.updateStatus(
                                id,
                                IncidentStatus.INVESTIGATING
                        )
                );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("RESOLVED", entity.getStatus());
    }
}
