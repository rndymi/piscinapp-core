package com.rndymi.es.piscinapp.core.supervision.api.dto;

import com.rndymi.es.piscinapp.core.execution.application.VisitObservationReference;
import com.rndymi.es.piscinapp.core.incidents.application.IncidentReference;
import com.rndymi.es.piscinapp.core.incidents.domain.IncidentStatus;
import com.rndymi.es.piscinapp.core.planning.application.VisitActivityExecutionReference;
import com.rndymi.es.piscinapp.core.planning.application.VisitExecutionReference;
import com.rndymi.es.piscinapp.core.planning.domain.VisitActivityStatus;
import com.rndymi.es.piscinapp.core.planning.domain.VisitStatus;
import com.rndymi.es.piscinapp.core.supervision.application.VisitSupervisionReference;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record VisitSupervisionResponse(
        VisitSummary visit,
        ExecutionSummary execution,
        List<ActivitySummary> activities,
        List<ObservationSummary> observations,
        List<IncidentSummary> incidents
) {

    public static VisitSupervisionResponse from(
            VisitSupervisionReference source
    ) {

        VisitExecutionReference visit =
                source.visit();

        return new VisitSupervisionResponse(
                VisitSummary.from(
                        visit
                ),
                ExecutionSummary.from(
                        visit
                ),
                visit.activities()
                        .stream()
                        .map(
                                ActivitySummary::from
                        )
                        .toList(),
                source.observations()
                        .stream()
                        .map(
                                ObservationSummary::from
                        )
                        .toList(),
                source.incidents()
                        .stream()
                        .map(
                                IncidentSummary::from
                        )
                        .toList()
        );
    }

    public record VisitSummary(
            UUID id,
            UUID poolId,
            UUID crewId,
            LocalDate plannedDate,
            LocalTime plannedTime,
            VisitStatus status,
            String notes
    ) {

        private static VisitSummary from(
                VisitExecutionReference source
        ) {

            return new VisitSummary(
                    source.id(),
                    source.poolId(),
                    source.crewId(),
                    source.plannedDate(),
                    source.plannedTime(),
                    source.status(),
                    source.notes()
            );
        }
    }

    public record ExecutionSummary(
            Instant startedAt,
            UUID startedByAccountId,
            UUID startedByEmployeeId,
            Instant completedAt,
            UUID completedByAccountId,
            UUID completedByEmployeeId
    ) {

        private static ExecutionSummary from(
                VisitExecutionReference source
        ) {

            return new ExecutionSummary(
                    source.startedAt(),
                    source.startedByAccountId(),
                    source.startedByEmployeeId(),
                    source.completedAt(),
                    source.completedByAccountId(),
                    source.completedByEmployeeId()
            );
        }
    }

    public record ActivitySummary(
            UUID maintenanceActivityId,
            VisitActivityStatus executionStatus,
            Instant completedAt,
            UUID completedByAccountId,
            UUID completedByEmployeeId
    ) {

        private static ActivitySummary from(
                VisitActivityExecutionReference source
        ) {

            return new ActivitySummary(
                    source.maintenanceActivityId(),
                    source.status(),
                    source.completedAt(),
                    source.completedByAccountId(),
                    source.completedByEmployeeId()
            );
        }
    }

    public record ObservationSummary(
            UUID id,
            String text,
            Instant createdAt,
            UUID createdByAccountId,
            UUID createdByEmployeeId
    ) {

        private static ObservationSummary from(
                VisitObservationReference source
        ) {

            return new ObservationSummary(
                    source.id(),
                    source.text(),
                    source.createdAt(),
                    source.createdByAccountId(),
                    source.createdByEmployeeId()
            );
        }
    }

    public record IncidentSummary(
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

        private static IncidentSummary from(
                IncidentReference source
        ) {

            return new IncidentSummary(
                    source.id(),
                    source.description(),
                    source.status(),
                    source.createdAt(),
                    source.createdByAccountId(),
                    source.createdByEmployeeId(),
                    source.resolvedAt(),
                    source.resolvedByAccountId(),
                    source.resolvedByEmployeeId()
            );
        }
    }
}
