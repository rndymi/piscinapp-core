package com.rndymi.es.piscinapp.core.planning.application;

import com.rndymi.es.piscinapp.core.planning.domain.VisitStatus;

import java.time.LocalDate;
import java.util.UUID;

public record VisitSearchCriteria(
        LocalDate date,
        LocalDate fromDate,
        LocalDate toDate,
        VisitStatus status,
        UUID poolId,
        UUID crewId
) {
}
