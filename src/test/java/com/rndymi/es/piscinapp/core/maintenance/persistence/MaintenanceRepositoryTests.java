package com.rndymi.es.piscinapp.core.maintenance.persistence;

import com.rndymi.es.piscinapp.core.maintenance.domain.MaintenanceActivity;
import com.rndymi.es.piscinapp.core.maintenance.domain.PoolMaintenanceActivity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MaintenanceRepositoryTests {

    @Autowired
    private MaintenanceActivityRepository
            maintenanceActivityRepository;

    @Autowired
    private PoolMaintenanceActivityRepository
            configurationRepository;

    @Test
    void shouldPersistMaintenanceActivity() {

        MaintenanceActivity activity =
                new MaintenanceActivity(
                        UUID.randomUUID(),
                        "Filter inspection",
                        "Check filter condition"
                );

        MaintenanceActivity saved =
                maintenanceActivityRepository
                        .saveAndFlush(
                                activity
                        );

        assertThat(
                maintenanceActivityRepository
                        .findById(
                                saved.getId()
                        )
        )
                .isPresent()
                .get()
                .satisfies(
                        persisted -> {

                            assertThat(
                                    persisted.getName()
                            )
                                    .isEqualTo(
                                            "Filter inspection"
                                    );

                            assertThat(
                                    persisted.isActive()
                            )
                                    .isTrue();
                        }
                );
    }

    @Test
    void shouldPersistPoolMaintenanceConfiguration() {

        UUID poolId =
                UUID.randomUUID();

        UUID activityId =
                UUID.randomUUID();

        PoolMaintenanceActivity configuration =
                new PoolMaintenanceActivity(
                        UUID.randomUUID(),
                        poolId,
                        activityId
                );

        configurationRepository
                .saveAndFlush(
                        configuration
                );

        assertThat(
                configurationRepository
                        .existsByPoolIdAndMaintenanceActivityId(
                                poolId,
                                activityId
                        )
        )
                .isTrue();
    }

    @Test
    void shouldRejectDuplicatedPoolMaintenanceConfiguration() {

        UUID poolId =
                UUID.randomUUID();

        UUID activityId =
                UUID.randomUUID();

        configurationRepository
                .saveAndFlush(
                        new PoolMaintenanceActivity(
                                UUID.randomUUID(),
                                poolId,
                                activityId
                        )
                );

        assertThatThrownBy(
                () ->
                        configurationRepository
                                .saveAndFlush(
                                        new PoolMaintenanceActivity(
                                                UUID.randomUUID(),
                                                poolId,
                                                activityId
                                        )
                                )
        )
                .isInstanceOf(
                        DataIntegrityViolationException.class
                );
    }
}
