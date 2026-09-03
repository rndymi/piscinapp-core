package com.rndymi.es.piscinapp.core.planning.application.exception;

import java.util.UUID;

public class VisitActivityNotFoundException
        extends RuntimeException {

    private final UUID visitId;
    private final UUID maintenanceActivityId;

    public VisitActivityNotFoundException(
            UUID visitId,
            UUID maintenanceActivityId
    ) {

        super(
                "Maintenance activity is not selected for the visit"
        );

        this.visitId =
                visitId;

        this.maintenanceActivityId =
                maintenanceActivityId;
    }

    public UUID getVisitId() {
        return visitId;
    }

    public UUID getMaintenanceActivityId() {
        return maintenanceActivityId;
    }
}
