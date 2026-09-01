package com.rndymi.es.piscinapp.core.crews.api.dto;

import com.rndymi.es.piscinapp.core.crews.domain.Crew;

import java.util.List;
import java.util.UUID;

public record CrewResponse(
        UUID id,
        String name,
        boolean active,
        UUID supervisorEmployeeId,
        List<UUID> memberIds
) {

    public static CrewResponse from(
            Crew crew,
            List<UUID> memberIds
    ) {

        return new CrewResponse(
                crew.getId(),
                crew.getName(),
                crew.isActive(),
                crew.getSupervisorEmployeeId(),
                List.copyOf(
                        memberIds
                )
        );
    }
}
