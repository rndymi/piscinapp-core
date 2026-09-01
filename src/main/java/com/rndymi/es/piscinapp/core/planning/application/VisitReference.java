package com.rndymi.es.piscinapp.core.planning.application;

import com.rndymi.es.piscinapp.core.planning.domain.VisitStatus;

import java.util.Set;
import java.util.UUID;

public record VisitReference(
        UUID id,
        UUID poolId,
        UUID crewId,
        VisitStatus status,
        Set<UUID> maintenanceActivityIds
) {
}
