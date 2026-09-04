package com.rndymi.es.piscinapp.core.planning.application;

import com.rndymi.es.piscinapp.core.planning.domain.VisitActivityStatus;

import java.time.Instant;
import java.util.UUID;

public record VisitActivityExecutionReference(
        UUID maintenanceActivityId,
        VisitActivityStatus status,
        Instant completedAt,
        UUID completedByAccountId,
        UUID completedByEmployeeId
) {
}
