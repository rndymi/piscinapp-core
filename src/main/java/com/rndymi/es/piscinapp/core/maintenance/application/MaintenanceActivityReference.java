package com.rndymi.es.piscinapp.core.maintenance.application;

import java.util.UUID;

public record MaintenanceActivityReference(
        UUID id,
        boolean active
) {
}
