
package com.cloudops.backend.service;

import com.cloudops.backend.repository.IncidentRepository;
import com.cloudops.backend.enums.IncidentStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentSearchTest {

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private IncidentService incidentService;

    @Test
    void shouldSearchIncidentsByKeyword() {

        when(incidentRepository.searchIncidents(
                isNull(),
                eq("Lambda"),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of()));

        incidentService.findAllPaged(
                0,
                5,
                null,
                "Lambda"
        );

        verify(incidentRepository).searchIncidents(
                isNull(),
                eq("Lambda"),
                any(Pageable.class)
        );
    }
}
