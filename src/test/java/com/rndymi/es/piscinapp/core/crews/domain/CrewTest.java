package com.rndymi.es.piscinapp.core.crews.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CrewTest {

    @Test
    void shouldCreateActiveCrewWithoutSupervisor() {

        UUID id =
                UUID.randomUUID();

        Crew crew =
                new Crew(
                        id,
                        "Morning Crew"
                );

        assertThat(
                crew.getId()
        )
                .isEqualTo(
                        id
                );

        assertThat(
                crew.getName()
        )
                .isEqualTo(
                        "Morning Crew"
                );

        assertThat(
                crew.isActive()
        )
                .isTrue();

        assertThat(
                crew.getSupervisorEmployeeId()
        )
                .isNull();
    }

    @Test
    void shouldRenameCrew() {

        Crew crew =
                new Crew(
                        UUID.randomUUID(),
                        "Morning Crew"
                );

        crew.rename(
                "North Zone Crew"
        );

        assertThat(
                crew.getName()
        )
                .isEqualTo(
                        "North Zone Crew"
                );
    }

    @Test
    void shouldActivateAndDeactivateCrew() {

        Crew crew =
                new Crew(
                        UUID.randomUUID(),
                        "Morning Crew"
                );

        crew.deactivate();

        assertThat(
                crew.isActive()
        )
                .isFalse();

        crew.activate();

        assertThat(
                crew.isActive()
        )
                .isTrue();
    }

    @Test
    void shouldAssignChangeAndClearSupervisor() {

        Crew crew =
                new Crew(
                        UUID.randomUUID(),
                        "Morning Crew"
                );

        UUID firstSupervisor =
                UUID.randomUUID();

        UUID secondSupervisor =
                UUID.randomUUID();

        crew.assignSupervisor(
                firstSupervisor
        );

        assertThat(
                crew.getSupervisorEmployeeId()
        )
                .isEqualTo(
                        firstSupervisor
                );

        crew.assignSupervisor(
                secondSupervisor
        );

        assertThat(
                crew.getSupervisorEmployeeId()
        )
                .isEqualTo(
                        secondSupervisor
                );

        crew.clearSupervisor();

        assertThat(
                crew.getSupervisorEmployeeId()
        )
                .isNull();
    }
}
