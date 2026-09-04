package com.rndymi.es.piscinapp.core.planning.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VisitMaintenanceActivityExecutionTest {

    @Test
    void shouldBeginPending() {

        VisitMaintenanceActivity activity =
                activity();

        assertThat(
                activity.getStatus()
        )
                .isEqualTo(
                        VisitActivityStatus.PENDING
                );

        assertThat(
                activity.getCompletedAt()
        )
                .isNull();
    }

    @Test
    void shouldCompletePendingActivity() {

        VisitMaintenanceActivity activity =
                activity();

        UUID accountId =
                UUID.randomUUID();

        UUID employeeId =
                UUID.randomUUID();

        Instant completedAt =
                Instant.parse(
                        "2026-09-04T00:30:00Z"
                );

        activity.complete(
                completedAt,
                accountId,
                employeeId
        );

        assertThat(
                activity.getStatus()
        )
                .isEqualTo(
                        VisitActivityStatus.COMPLETED
                );

        assertThat(
                activity.getCompletedAt()
        )
                .isEqualTo(
                        completedAt
                );

        assertThat(
                activity.getCompletedByAccountId()
        )
                .isEqualTo(
                        accountId
                );

        assertThat(
                activity.getCompletedByEmployeeId()
        )
                .isEqualTo(
                        employeeId
                );
    }

    @Test
    void shouldRejectDuplicateCompletion() {

        VisitMaintenanceActivity activity =
                activity();

        activity.complete(
                Instant.parse(
                        "2026-09-04T00:30:00Z"
                ),
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        assertThatThrownBy(
                () ->
                        activity.complete(
                                Instant.parse(
                                        "2026-09-04T00:40:00Z"
                                ),
                                UUID.randomUUID(),
                                UUID.randomUUID()
                        )
        )
                .isInstanceOf(
                        IllegalStateException.class
                );
    }

    private VisitMaintenanceActivity activity() {

        return new VisitMaintenanceActivity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }
}
