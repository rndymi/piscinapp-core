package com.rndymi.es.piscinapp.core.planning.application;

import com.rndymi.es.piscinapp.core.planning.domain.VisitStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record VisitExecutionReference(
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
        List<VisitActivityExecutionReference> activities
) {
}
