package com.rndymi.es.piscinapp.core.planning.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VisitExecutionDomainTest {

    @Test
    void shouldStartPlannedVisit() {

        Visit visit =
                visit();

        UUID accountId =
                UUID.randomUUID();

        UUID employeeId =
                UUID.randomUUID();

        Instant startedAt =
                Instant.parse(
                        "2026-09-04T00:00:00Z"
                );

        visit.start(
                startedAt,
                accountId,
                employeeId
        );

        assertThat(
                visit.getStatus()
        )
                .isEqualTo(
                        VisitStatus.IN_PROGRESS
                );

        assertThat(
                visit.getStartedAt()
        )
                .isEqualTo(
                        startedAt
                );

        assertThat(
                visit.getStartedByAccountId()
        )
                .isEqualTo(
                        accountId
                );

        assertThat(
                visit.getStartedByEmployeeId()
        )
                .isEqualTo(
                        employeeId
                );
    }

    @Test
    void shouldRejectSecondStart() {

        Visit visit =
                visit();

        visit.start(
                Instant.parse(
                        "2026-09-04T00:00:00Z"
                ),
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        assertThatThrownBy(
                () ->
                        visit.start(
                                Instant.parse(
                                        "2026-09-04T00:01:00Z"
                                ),
                                UUID.randomUUID(),
                                UUID.randomUUID()
                        )
        )
                .isInstanceOf(
                        IllegalStateException.class
                );
    }

    @Test
    void shouldCompleteInProgressVisit() {

        Visit visit =
                visit();

        visit.start(
                Instant.parse(
                        "2026-09-04T00:00:00Z"
                ),
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        UUID accountId =
                UUID.randomUUID();

        UUID employeeId =
                UUID.randomUUID();

        Instant completedAt =
                Instant.parse(
                        "2026-09-04T01:00:00Z"
                );

        visit.complete(
                completedAt,
                accountId,
                employeeId
        );

        assertThat(
                visit.getStatus()
        )
                .isEqualTo(
                        VisitStatus.COMPLETED
                );

        assertThat(
                visit.getCompletedAt()
        )
                .isEqualTo(
                        completedAt
                );

        assertThat(
                visit.getCompletedByAccountId()
        )
                .isEqualTo(
                        accountId
                );

        assertThat(
                visit.getCompletedByEmployeeId()
        )
                .isEqualTo(
                        employeeId
                );
    }

    @Test
    void shouldRejectDirectPlannedToCompleted() {

        Visit visit =
                visit();

        assertThatThrownBy(
                () ->
                        visit.complete(
                                Instant.parse(
                                        "2026-09-04T01:00:00Z"
                                ),
                                UUID.randomUUID(),
                                UUID.randomUUID()
                        )
        )
                .isInstanceOf(
                        IllegalStateException.class
                );
    }

    private Visit visit() {

        return new Visit(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.of(
                        2099,
                        9,
                        4
                ),
                LocalTime.of(
                        9,
                        0
                ),
                null
        );
    }
}
