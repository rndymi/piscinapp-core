package com.rndymi.es.piscinapp.core.maintenance.application;

import com.rndymi.es.piscinapp.core.maintenance.application.exception.InactiveResourceException;
import com.rndymi.es.piscinapp.core.maintenance.application.exception.PoolMaintenanceActivityConflictException;
import com.rndymi.es.piscinapp.core.maintenance.domain.MaintenanceActivity;
import com.rndymi.es.piscinapp.core.maintenance.domain.PoolMaintenanceActivity;
import com.rndymi.es.piscinapp.core.maintenance.persistence.MaintenanceActivityRepository;
import com.rndymi.es.piscinapp.core.maintenance.persistence.MaintenanceActivitySpecifications;
import com.rndymi.es.piscinapp.core.maintenance.persistence.PoolMaintenanceActivityRepository;
import com.rndymi.es.piscinapp.core.pools.application.PoolLookup;
import com.rndymi.es.piscinapp.core.pools.application.PoolReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PoolMaintenanceConfigurationService {

    private static final String
            CONFIGURATION_CONSTRAINT =
            "uk_pool_maintenance_activity";

    private final PoolLookup
            poolLookup;

    private final MaintenanceActivityService
            maintenanceActivityService;

    private final MaintenanceActivityRepository
            maintenanceActivityRepository;

    private final PoolMaintenanceActivityRepository
            configurationRepository;

    public PoolMaintenanceConfigurationService(
            PoolLookup poolLookup,
            MaintenanceActivityService maintenanceActivityService,
            MaintenanceActivityRepository maintenanceActivityRepository,
            PoolMaintenanceActivityRepository configurationRepository
    ) {

        this.poolLookup =
                poolLookup;

        this.maintenanceActivityService =
                maintenanceActivityService;

        this.maintenanceActivityRepository =
                maintenanceActivityRepository;

        this.configurationRepository =
                configurationRepository;
    }

    @Transactional
    public void configure(
            UUID poolId,
            UUID activityId
    ) {

        PoolReference pool =
                poolLookup
                        .requirePool(
                                poolId
                        );

        if (!pool.active()) {

            throw new InactiveResourceException(
                    "Swimming pool",
                    poolId
            );
        }

        MaintenanceActivity activity =
                maintenanceActivityService
                        .getActivityForConfiguration(
                                activityId
                        );

        if (!activity.isActive()) {

            throw new InactiveResourceException(
                    "Maintenance activity",
                    activityId
            );
        }

        if (
                configurationRepository
                        .existsByPoolIdAndMaintenanceActivityId(
                                poolId,
                                activityId
                        )
        ) {

            throw new PoolMaintenanceActivityConflictException(
                    poolId,
                    activityId
            );
        }

        try {

            configurationRepository
                    .saveAndFlush(
                            new PoolMaintenanceActivity(
                                    UUID.randomUUID(),
                                    poolId,
                                    activityId
                            )
                    );

        } catch (
                DataIntegrityViolationException
                        exception
        ) {

            if (
                    isConfigurationConstraintViolation(
                            exception
                    )
            ) {

                throw new PoolMaintenanceActivityConflictException(
                        poolId,
                        activityId
                );
            }

            throw exception;
        }
    }

    @Transactional
    public void remove(
            UUID poolId,
            UUID activityId
    ) {

        poolLookup
                .requirePool(
                        poolId
                );

        maintenanceActivityService
                .getActivityForConfiguration(
                        activityId
                );

        configurationRepository
                .deleteByPoolIdAndMaintenanceActivityId(
                        poolId,
                        activityId
                );
    }

    @Transactional(readOnly = true)
    public Page<MaintenanceActivity>
    listConfiguredActivities(
            UUID poolId,
            Boolean active,
            String search,
            Pageable pageable
    ) {

        poolLookup
                .requirePool(
                        poolId
                );

        Specification<MaintenanceActivity>
                specification =
                MaintenanceActivitySpecifications
                        .applicableToPool(
                                poolId
                        )
                        .and(
                                MaintenanceActivitySpecifications
                                        .activeEquals(
                                                active
                                        )
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

    private boolean
    isConfigurationConstraintViolation(
            DataIntegrityViolationException exception
    ) {

        Throwable current =
                exception;

        while (current != null) {

            String message =
                    current.getMessage();

            if (
                    message != null
                            &&
                            message.contains(
                                    CONFIGURATION_CONSTRAINT
                            )
            ) {

                return true;
            }

            current =
                    current.getCause();
        }

        return false;
    }
}
