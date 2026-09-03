package com.rndymi.es.piscinapp.core.maintenance.application;

import com.rndymi.es.piscinapp.core.maintenance.application.exception.MaintenanceActivityNotFoundException;
import com.rndymi.es.piscinapp.core.maintenance.domain.MaintenanceActivity;
import com.rndymi.es.piscinapp.core.maintenance.persistence.MaintenanceActivityRepository;
import com.rndymi.es.piscinapp.core.maintenance.persistence.PoolMaintenanceActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaintenancePlanningLookupService
        implements MaintenancePlanningLookup {

    private final MaintenanceActivityRepository maintenanceActivityRepository;
    private final PoolMaintenanceActivityRepository poolMaintenanceActivityRepository;

    @Override
    @Transactional(readOnly = true)
    public MaintenanceActivityReference requireActivity(
            UUID activityId
    ) {

        MaintenanceActivity activity =
                maintenanceActivityRepository
                        .findById(
                                activityId
                        )
                        .orElseThrow(
                                () ->
                                        new MaintenanceActivityNotFoundException(
                                                activityId
                                        )
                        );

        return new MaintenanceActivityReference(
                activity.getId(),
                activity.isActive()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isApplicable(
            UUID poolId,
            UUID activityId
    ) {

        return poolMaintenanceActivityRepository
                .existsByPoolIdAndMaintenanceActivityId(
                        poolId,
                        activityId
                );
    }
}
