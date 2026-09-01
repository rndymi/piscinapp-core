package com.rndymi.es.piscinapp.core.maintenance.api.dto;

import com.rndymi.es.piscinapp.core.maintenance.domain.MaintenanceActivity;

import java.util.UUID;

public record MaintenanceActivityResponse(
        UUID id,
        String name,
        String description,
        boolean active
) {

    public static MaintenanceActivityResponse from(
            MaintenanceActivity activity
    ) {

        return new MaintenanceActivityResponse(
                activity.getId(),
                activity.getName(),
                activity.getDescription(),
                activity.isActive()
        );
    }
}
