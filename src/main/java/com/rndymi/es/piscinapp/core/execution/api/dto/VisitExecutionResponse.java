package com.rndymi.es.piscinapp.core.execution.api.dto;

import com.rndymi.es.piscinapp.core.planning.application.VisitExecutionReference;
import com.rndymi.es.piscinapp.core.planning.domain.VisitStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record VisitExecutionResponse(
        UUID id,
        UUID poolId,
        UUID crewId,
        LocalDate plannedDate,
        LocalTime plannedTime,
        VisitStatus status,
        String notes,
        Instant startedAt,
        UUID startedByAccountId,
        UUID startedByEmployeeId,
        Instant completedAt,
        UUID completedByAccountId,
        UUID completedByEmployeeId,
        List<VisitActivityExecutionResponse> activities
) {

    public static VisitExecutionResponse from(
            VisitExecutionReference source
    ) {

        return new VisitExecutionResponse(
                source.id(),
                source.poolId(),
                source.crewId(),
                source.plannedDate(),
                source.plannedTime(),
                source.status(),
                source.notes(),
                source.startedAt(),
                source.startedByAccountId(),
                source.startedByEmployeeId(),
                source.completedAt(),
                source.completedByAccountId(),
                source.completedByEmployeeId(),
                source.activities()
                        .stream()
                        .map(
                                VisitActivityExecutionResponse::from
                        )
                        .toList()
        );
    }
}
