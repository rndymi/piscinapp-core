package com.rndymi.es.piscinapp.core.planning.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public interface VisitExecutionOperations {

    VisitExecutionReference requireExecutionVisit(
            UUID visitId
    );

    VisitExecutionReference startVisit(
            UUID visitId,
            Instant startedAt,
            UUID accountId,
            UUID employeeId
    );

    VisitActivityExecutionReference completeActivity(
            UUID visitId,
            UUID maintenanceActivityId,
            Instant completedAt,
            UUID accountId,
            UUID employeeId
    );

    VisitExecutionReference completeVisit(
            UUID visitId,
            Instant completedAt,
            UUID accountId,
            UUID employeeId
    );

    Page<VisitExecutionReference> findAssignedVisits(
            Set<UUID> crewIds,
            LocalDate date,
            LocalDate fromDate,
            LocalDate toDate,
            com.rndymi.es.piscinapp.core.planning.domain.VisitStatus status,
            Pageable pageable
    );
}
