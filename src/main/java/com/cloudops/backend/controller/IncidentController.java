
package com.cloudops.backend.controller;

import com.cloudops.backend.dto.CreateIncidentRequest;
import com.cloudops.backend.dto.IncidentResponse;
import com.cloudops.backend.enums.IncidentStatus;
import com.cloudops.backend.service.IncidentService;
import com.cloudops.backend.dto.UpdateIncidentStatusRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

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




    @GetMapping("/paged")
    public Page<IncidentResponse> findAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) IncidentStatus status,
            @RequestParam(required = false) String keyword) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "page must be >= 0 and size must be between 1 and 100"
            );
        }

        return incidentService.findAllPaged(
                page,
                size,
                status,
                keyword
        );
    }





    @PatchMapping("/{id}/status")
    public IncidentResponse updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateIncidentStatusRequest request) {

        return incidentService.updateStatus(id, request.status());
    }

}
