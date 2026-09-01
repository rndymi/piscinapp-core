package com.rndymi.es.piscinapp.core.planning.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VisitTests {

    @Test
    void shouldCreatePlannedVisit() {

        UUID id =
                UUID.randomUUID();

        UUID poolId =
                UUID.randomUUID();

        UUID crewId =
                UUID.randomUUID();

        Visit visit =
                new Visit(
                        id,
                        poolId,
                        crewId,
                        LocalDate.of(
                                2026,
                                9,
                                3
                        ),
                        LocalTime.of(
                                9,
                                30
                        ),
                        "Use side entrance."
                );

        assertThat(
                visit.getId()
        )
                .isEqualTo(
                        id
                );

        assertThat(
                visit.getPoolId()
        )
                .isEqualTo(
                        poolId
                );

        assertThat(
                visit.getCrewId()
        )
                .isEqualTo(
                        crewId
                );

        assertThat(
                visit.getStatus()
        )
                .isEqualTo(
                        VisitStatus.PLANNED
                );
    }

    @Test
    void shouldUpdatePlannedVisit() {

        Visit visit =
                visit();

        UUID newPoolId =
                UUID.randomUUID();

        UUID newCrewId =
                UUID.randomUUID();

        visit.updatePlanning(
                newPoolId,
                newCrewId,
                LocalDate.of(
                        2026,
                        9,
                        5
                ),
                LocalTime.of(
                        11,
                        0
                ),
                "Updated notes"
        );

        assertThat(
                visit.getPoolId()
        )
                .isEqualTo(
                        newPoolId
                );

        assertThat(
                visit.getCrewId()
        )
                .isEqualTo(
                        newCrewId
                );

        assertThat(
                visit.getPlannedDate()
        )
                .isEqualTo(
                        LocalDate.of(
                                2026,
                                9,
                                5
                        )
                );
    }

    @Test
    void shouldCancelPlannedVisit() {

        Visit visit =
                visit();

        visit.cancel();

        assertThat(
                visit.getStatus()
        )
                .isEqualTo(
                        VisitStatus.CANCELLED
                );
    }

    @Test
    void shouldRejectPlanningMutationAfterCancellation() {

        Visit visit =
                visit();

        visit.cancel();

        assertThatThrownBy(
                () ->
                        visit.updatePlanning(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                LocalDate.of(
                                        2026,
                                        9,
                                        5
                                ),
                                LocalTime.of(
                                        10,
                                        0
                                ),
                                null
                        )
        )
                .isInstanceOf(
                        IllegalStateException.class
                );
    }

    @Test
    void shouldRejectSecondCancellation() {

        Visit visit =
                visit();

        visit.cancel();

        assertThatThrownBy(
                visit::cancel
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
                        2026,
                        9,
                        3
                ),
                LocalTime.of(
                        9,
                        30
                ),
                null
        );
    }
}
