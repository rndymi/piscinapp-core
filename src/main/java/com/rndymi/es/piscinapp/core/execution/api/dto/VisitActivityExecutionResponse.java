package com.rndymi.es.piscinapp.core.execution.api.dto;

import com.rndymi.es.piscinapp.core.planning.application.VisitActivityExecutionReference;
import com.rndymi.es.piscinapp.core.planning.domain.VisitActivityStatus;

import java.time.Instant;
import java.util.UUID;

public record VisitActivityExecutionResponse(
        UUID maintenanceActivityId,
        VisitActivityStatus status,
        Instant completedAt,
        UUID completedByAccountId,
        UUID completedByEmployeeId
) {

    public static VisitActivityExecutionResponse from(
            VisitActivityExecutionReference source
    ) {

        return new VisitActivityExecutionResponse(
                source.maintenanceActivityId(),
                source.status(),
                source.completedAt(),
                source.completedByAccountId(),
                source.completedByEmployeeId()
        );
    }
}
