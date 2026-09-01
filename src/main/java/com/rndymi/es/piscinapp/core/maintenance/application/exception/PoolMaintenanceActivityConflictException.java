package com.rndymi.es.piscinapp.core.maintenance.application.exception;

import java.util.UUID;

public class PoolMaintenanceActivityConflictException
        extends RuntimeException {

    public PoolMaintenanceActivityConflictException(
            UUID poolId,
            UUID activityId
    ) {

        super(
                "Maintenance activity "
                        + activityId
                        + " is already configured for swimming pool "
                        + poolId
        );
    }
}
