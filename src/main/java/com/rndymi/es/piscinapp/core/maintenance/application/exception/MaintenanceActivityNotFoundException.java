package com.rndymi.es.piscinapp.core.maintenance.application.exception;

import java.util.UUID;

public class MaintenanceActivityNotFoundException
        extends RuntimeException {

    public MaintenanceActivityNotFoundException(
            UUID activityId
    ) {

        super(
                "Maintenance activity not found: "
                        + activityId
        );
    }
}
