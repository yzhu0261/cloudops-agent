
package com.cloudops.backend.controller;

import com.cloudops.backend.dto.CreateIncidentRequest;
import com.cloudops.backend.dto.IncidentResponse;
import com.cloudops.backend.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.cloudops.backend.dto.UpdateIncidentStatusRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IncidentResponse create(
            @Valid @RequestBody CreateIncidentRequest request) {
        return incidentService.create(request);
    }

    @GetMapping
    public List<IncidentResponse> findAll() {
        return incidentService.findAll();
    }

    @GetMapping("/{id}")
    public IncidentResponse findById(@PathVariable UUID id) {
        return incidentService.findById(id);
    }


    @PatchMapping("/{id}/status")
    public IncidentResponse updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateIncidentStatusRequest request) {

        return incidentService.updateStatus(id, request.status());
    }

}
