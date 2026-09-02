package com.rndymi.es.piscinapp.core.maintenance.application;

import java.util.UUID;

public interface MaintenancePlanningLookup {

    MaintenanceActivityReference requireActivity(
            UUID activityId
    );

    boolean isApplicable(
            UUID poolId,
            UUID activityId
    );
}
