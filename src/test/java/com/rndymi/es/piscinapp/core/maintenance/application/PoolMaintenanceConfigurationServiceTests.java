package com.rndymi.es.piscinapp.core.maintenance.application;

import com.rndymi.es.piscinapp.core.platform.application.InactiveResourceException;
import com.rndymi.es.piscinapp.core.maintenance.application.exception.PoolMaintenanceActivityConflictException;
import com.rndymi.es.piscinapp.core.maintenance.domain.MaintenanceActivity;
import com.rndymi.es.piscinapp.core.maintenance.persistence.PoolMaintenanceActivityRepository;
import com.rndymi.es.piscinapp.core.pools.application.SwimmingPoolService;
import com.rndymi.es.piscinapp.core.pools.domain.SwimmingPool;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class PoolMaintenanceConfigurationServiceTests {

    @Autowired
    private SwimmingPoolService
            swimmingPoolService;

    @Autowired
    private MaintenanceActivityService
            maintenanceActivityService;

    @Autowired
    private PoolMaintenanceConfigurationService
            configurationService;

    @Autowired
    private PoolMaintenanceActivityRepository
            configurationRepository;

    @Test
    void shouldConfigureActivityForActivePool() {

        SwimmingPool pool =
                swimmingPoolService
                        .createPool(
                                "Residencial Norte",
                                "Calle Example 10, Madrid"
                        );

        MaintenanceActivity activity =
                maintenanceActivityService
                        .createActivity(
                                "Filter inspection",
                                null
                        );

        configurationService
                .configure(
                        pool.getId(),
                        activity.getId()
                );

        assertThat(
                configurationRepository
                        .existsByPoolIdAndMaintenanceActivityId(
                                pool.getId(),
                                activity.getId()
                        )
        )
                .isTrue();
    }

    @Test
    void shouldRejectDuplicatedConfiguration() {

        SwimmingPool pool =
                swimmingPoolService
                        .createPool(
                                "Residencial Norte",
                                "Calle Example 10, Madrid"
                        );

        MaintenanceActivity activity =
                maintenanceActivityService
                        .createActivity(
                                "Filter inspection",
                                null
                        );

        configurationService
                .configure(
                        pool.getId(),
                        activity.getId()
                );

        UUID poolId =
                pool.getId();

        UUID activityId =
                activity.getId();

        assertThatThrownBy(
                () ->
                        configurationService
                                .configure(
                                        poolId,
                                        activityId
                                )
        )
                .isInstanceOf(
                        PoolMaintenanceActivityConflictException.class
                );
    }

    @Test
    void shouldRejectConfigurationForInactivePool() {

        SwimmingPool pool =
                swimmingPoolService
                        .createPool(
                                "Residencial Norte",
                                "Calle Example 10, Madrid"
                        );

        swimmingPoolService
                .updateStatus(
                        pool.getId(),
                        false
                );

        MaintenanceActivity activity =
                maintenanceActivityService
                        .createActivity(
                                "Filter inspection",
                                null
                        );

        UUID poolId =
                pool.getId();

        UUID activityId =
                activity.getId();

        assertThatThrownBy(
                () ->
                        configurationService
                                .configure(
                                        poolId,
                                        activityId
                                )
        )
                .isInstanceOf(
                        InactiveResourceException.class
                );
    }

    @Test
    void shouldRejectConfigurationForInactiveActivity() {

        SwimmingPool pool =
                swimmingPoolService
                        .createPool(
                                "Residencial Norte",
                                "Calle Example 10, Madrid"
                        );

        MaintenanceActivity activity =
                maintenanceActivityService
                        .createActivity(
                                "Filter inspection",
                                null
                        );

        maintenanceActivityService
                .updateStatus(
                        activity.getId(),
                        false
                );

        UUID poolId =
                pool.getId();

        UUID activityId =
                activity.getId();

        assertThatThrownBy(
                () ->
                        configurationService
                                .configure(
                                        poolId,
                                        activityId
                                )
        )
                .isInstanceOf(
                        InactiveResourceException.class
                );
    }

    @Test
    void shouldPreserveConfigurationAfterDeactivation() {

        SwimmingPool pool =
                swimmingPoolService
                        .createPool(
                                "Residencial Norte",
                                "Calle Example 10, Madrid"
                        );

        MaintenanceActivity activity =
                maintenanceActivityService
                        .createActivity(
                                "Filter inspection",
                                null
                        );

        configurationService
                .configure(
                        pool.getId(),
                        activity.getId()
                );

        swimmingPoolService
                .updateStatus(
                        pool.getId(),
                        false
                );

        maintenanceActivityService
                .updateStatus(
                        activity.getId(),
                        false
                );

        assertThat(
                configurationRepository
                        .existsByPoolIdAndMaintenanceActivityId(
                                pool.getId(),
                                activity.getId()
                        )
        )
                .isTrue();
    }

    @Test
    void shouldRemoveOnlyApplicability() {

        SwimmingPool pool =
                swimmingPoolService
                        .createPool(
                                "Residencial Norte",
                                "Calle Example 10, Madrid"
                        );

        MaintenanceActivity activity =
                maintenanceActivityService
                        .createActivity(
                                "Filter inspection",
                                null
                        );

        configurationService
                .configure(
                        pool.getId(),
                        activity.getId()
                );

        configurationService
                .remove(
                        pool.getId(),
                        activity.getId()
                );

        assertThat(
                configurationRepository
                        .existsByPoolIdAndMaintenanceActivityId(
                                pool.getId(),
                                activity.getId()
                        )
        )
                .isFalse();

        assertThat(
                swimmingPoolService
                        .getPool(
                                pool.getId()
                        )
        )
                .isNotNull();

        assertThat(
                maintenanceActivityService
                        .getActivity(
                                activity.getId()
                        )
        )
                .isNotNull();
    }
}
