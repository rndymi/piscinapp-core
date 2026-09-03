package com.rndymi.es.piscinapp.core.crews.persistence;

import com.rndymi.es.piscinapp.core.crews.domain.Crew;
import com.rndymi.es.piscinapp.core.crews.domain.CrewMembership;
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
class CrewRepositoryTests {

    @Autowired
    private CrewRepository
            crewRepository;

    @Autowired
    private CrewMembershipRepository
            crewMembershipRepository;

    @Test
    void shouldPersistCrew() {

        UUID id =
                UUID.randomUUID();

        Crew crew =
                crewRepository
                        .saveAndFlush(
                                new Crew(
                                        id,
                                        "Morning Crew"
                                )
                        );

        assertThat(
                crewRepository
                        .findById(
                                id
                        )
        )
                .contains(
                        crew
                );
    }

    @Test
    void shouldPersistNullableSupervisor() {

        Crew crew =
                crewRepository
                        .saveAndFlush(
                                new Crew(
                                        UUID.randomUUID(),
                                        "Morning Crew"
                                )
                        );

        assertThat(
                crew.getSupervisorEmployeeId()
        )
                .isNull();
    }

    @Test
    void shouldEnforceUniqueCrewEmployeePair() {

        UUID crewId =
                UUID.randomUUID();

        UUID employeeId =
                UUID.randomUUID();

        crewRepository
                .saveAndFlush(
                        new Crew(
                                crewId,
                                "Morning Crew"
                        )
                );

        crewMembershipRepository
                .saveAndFlush(
                        new CrewMembership(
                                UUID.randomUUID(),
                                crewId,
                                employeeId
                        )
                );

        CrewMembership duplicate =
                new CrewMembership(
                        UUID.randomUUID(),
                        crewId,
                        employeeId
                );

        assertThatThrownBy(
                () ->
                        crewMembershipRepository
                                .saveAndFlush(
                                        duplicate
                                )
        )
                .isInstanceOf(
                        DataIntegrityViolationException.class
                );
    }

    @Test
    void shouldAllowSameEmployeeInDifferentCrews() {

        UUID employeeId =
                UUID.randomUUID();

        Crew firstCrew =
                crewRepository
                        .saveAndFlush(
                                new Crew(
                                        UUID.randomUUID(),
                                        "Morning Crew"
                                )
                        );

        Crew secondCrew =
                crewRepository
                        .saveAndFlush(
                                new Crew(
                                        UUID.randomUUID(),
                                        "Evening Crew"
                                )
                        );

        crewMembershipRepository
                .saveAndFlush(
                        new CrewMembership(
                                UUID.randomUUID(),
                                firstCrew.getId(),
                                employeeId
                        )
                );

        crewMembershipRepository
                .saveAndFlush(
                        new CrewMembership(
                                UUID.randomUUID(),
                                secondCrew.getId(),
                                employeeId
                        )
                );

        assertThat(
                crewMembershipRepository
                        .existsByCrewIdAndEmployeeId(
                                firstCrew.getId(),
                                employeeId
                        )
        )
                .isTrue();

        assertThat(
                crewMembershipRepository
                        .existsByCrewIdAndEmployeeId(
                                secondCrew.getId(),
                                employeeId
                        )
        )
                .isTrue();
    }
}
