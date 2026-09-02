package com.rndymi.es.piscinapp.core.planning.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rndymi.es.piscinapp.core.planning.domain.Visit;
import com.rndymi.es.piscinapp.core.planning.domain.VisitStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record VisitResponse(
        UUID id,
        UUID poolId,
        UUID crewId,
        LocalDate plannedDate,
        @JsonFormat(pattern = "HH:mm")
        LocalTime plannedTime,
        VisitStatus status,
        List<UUID> maintenanceActivityIds,
        String notes
) {

    public static VisitResponse from(
            Visit visit,
            List<UUID> maintenanceActivityIds
    ) {

        return new VisitResponse(
                visit.getId(),
                visit.getPoolId(),
                visit.getCrewId(),
                visit.getPlannedDate(),
                visit.getPlannedTime(),
                visit.getStatus(),
                maintenanceActivityIds,
                visit.getNotes()
        );
    }
}
