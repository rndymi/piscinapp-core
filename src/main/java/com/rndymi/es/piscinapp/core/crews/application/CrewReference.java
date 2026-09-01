package com.rndymi.es.piscinapp.core.crews.application;

import java.util.Set;
import java.util.UUID;

public record CrewReference(
        UUID id,
        boolean active,
        UUID supervisorEmployeeId,
        Set<UUID> memberIds
) {
}
