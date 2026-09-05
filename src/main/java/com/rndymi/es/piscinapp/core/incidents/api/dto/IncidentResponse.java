package com.rndymi.es.piscinapp.core.incidents.api.dto;

import com.rndymi.es.piscinapp.core.incidents.domain.Incident;
import com.rndymi.es.piscinapp.core.incidents.domain.IncidentStatus;

import java.time.Instant;
import java.util.UUID;

public record IncidentResponse(
        UUID id,
        UUID visitId,
        String description,
        IncidentStatus status,
        Instant createdAt,
        UUID createdByAccountId,
        UUID createdByEmployeeId,
        Instant resolvedAt,
        UUID resolvedByAccountId,
        UUID resolvedByEmployeeId
) {

    public static IncidentResponse from(
            Incident incident
    ) {

        return new IncidentResponse(
                incident.getId(),
                incident.getVisitId(),
                incident.getDescription(),
                incident.getStatus(),
                incident.getCreatedAt(),
                incident.getCreatedByAccountId(),
                incident.getCreatedByEmployeeId(),
                incident.getResolvedAt(),
                incident.getResolvedByAccountId(),
                incident.getResolvedByEmployeeId()
        );
    }
}
