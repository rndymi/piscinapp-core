package com.rndymi.es.piscinapp.core.incidents.application;

import com.rndymi.es.piscinapp.core.incidents.domain.IncidentStatus;

import java.time.Instant;
import java.util.UUID;

public record IncidentReference(
        UUID id,
        String description,
        IncidentStatus status,
        Instant createdAt,
        UUID createdByAccountId,
        UUID createdByEmployeeId,
        Instant resolvedAt,
        UUID resolvedByAccountId,
        UUID resolvedByEmployeeId
) {
}
