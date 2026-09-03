package com.rndymi.es.piscinapp.core.planning.application.exception;

import com.rndymi.es.piscinapp.core.planning.domain.VisitActivityStatus;

import java.util.UUID;

public class VisitActivityStateConflictException
        extends RuntimeException {

    private final UUID visitId;
    private final UUID maintenanceActivityId;
    private final VisitActivityStatus currentStatus;

    public VisitActivityStateConflictException(
            UUID visitId,
            UUID maintenanceActivityId,
            VisitActivityStatus currentStatus
    ) {

        super(
                "Visit maintenance activity state conflict"
        );

        this.visitId =
                visitId;

        this.maintenanceActivityId =
                maintenanceActivityId;

        this.currentStatus =
                currentStatus;
    }

    public UUID getVisitId() {
        return visitId;
    }

    public UUID getMaintenanceActivityId() {
        return maintenanceActivityId;
    }

    public VisitActivityStatus getCurrentStatus() {
        return currentStatus;
    }
}
