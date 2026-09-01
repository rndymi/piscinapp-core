package com.rndymi.es.piscinapp.core.maintenance.application;

import com.rndymi.es.piscinapp.core.maintenance.application.exception.MaintenanceActivityNotFoundException;
import com.rndymi.es.piscinapp.core.maintenance.domain.MaintenanceActivity;
import com.rndymi.es.piscinapp.core.maintenance.persistence.MaintenanceActivityRepository;
import com.rndymi.es.piscinapp.core.maintenance.persistence.MaintenanceActivitySpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MaintenanceActivityService {

    private final MaintenanceActivityRepository
            maintenanceActivityRepository;

    public MaintenanceActivityService(
            MaintenanceActivityRepository maintenanceActivityRepository
    ) {

        this.maintenanceActivityRepository =
                maintenanceActivityRepository;
    }

    @Transactional
    public MaintenanceActivity createActivity(
            String name,
            String description
    ) {

        MaintenanceActivity activity =
                new MaintenanceActivity(
                        UUID.randomUUID(),
                        normalizeName(
                                name
                        ),
                        normalizeDescription(
                                description
                        )
                );

        return maintenanceActivityRepository
                .saveAndFlush(
                        activity
                );
    }

    @Transactional(readOnly = true)
    public MaintenanceActivity getActivity(
            UUID activityId
    ) {

        return maintenanceActivityRepository
                .findById(
                        activityId
                )
                .orElseThrow(
                        () ->
                                new MaintenanceActivityNotFoundException(
                                        activityId
                                )
                );
    }

    @Transactional(readOnly = true)
    public Page<MaintenanceActivity> listActivities(
            Boolean active,
            String search,
            Pageable pageable
    ) {

        Specification<MaintenanceActivity>
                specification =
                MaintenanceActivitySpecifications
                        .activeEquals(
                                active
                        )
                        .and(
                                MaintenanceActivitySpecifications
                                        .nameOrDescriptionContains(
                                                search
                                        )
                        );

        return maintenanceActivityRepository
                .findAll(
                        specification,
                        pageable
                );
    }

    @Transactional
    public MaintenanceActivity updateActivity(
            UUID activityId,
            String name,
            String description
    ) {

        MaintenanceActivity activity =
                getActivityForUpdate(
                        activityId
                );

        activity.update(
                normalizeName(
                        name
                ),
                normalizeDescription(
                        description
                )
        );

        return activity;
    }

    @Transactional
    public MaintenanceActivity updateStatus(
            UUID activityId,
            boolean active
    ) {

        MaintenanceActivity activity =
                getActivityForUpdate(
                        activityId
                );

        if (active) {

            activity.activate();

        } else {

            activity.deactivate();
        }

        return activity;
    }

    MaintenanceActivity getActivityForConfiguration(
            UUID activityId
    ) {

        return maintenanceActivityRepository
                .findById(
                        activityId
                )
                .orElseThrow(
                        () ->
                                new MaintenanceActivityNotFoundException(
                                        activityId
                                )
                );
    }

    private MaintenanceActivity getActivityForUpdate(
            UUID activityId
    ) {

        return maintenanceActivityRepository
                .findById(
                        activityId
                )
                .orElseThrow(
                        () ->
                                new MaintenanceActivityNotFoundException(
                                        activityId
                                )
                );
    }

    private String normalizeName(
            String value
    ) {

        return value.strip();
    }

    private String normalizeDescription(
            String value
    ) {

        if (
                value == null
                        ||
                        value.isBlank()
        ) {

            return null;
        }

        return value.strip();
    }
}
