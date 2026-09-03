package com.rndymi.es.piscinapp.core.crews.application;

import com.rndymi.es.piscinapp.core.crews.application.exception.CrewMemberNotFoundException;
import com.rndymi.es.piscinapp.core.crews.application.exception.CrewMembershipConflictException;
import com.rndymi.es.piscinapp.core.crews.application.exception.CrewSupervisorConflictException;
import com.rndymi.es.piscinapp.core.crews.domain.Crew;
import com.rndymi.es.piscinapp.core.crews.persistence.CrewMembershipRepository;
import com.rndymi.es.piscinapp.core.crews.persistence.CrewRepository;
import com.rndymi.es.piscinapp.core.employees.application.EmployeeService;
import com.rndymi.es.piscinapp.core.employees.domain.Employee;
import com.rndymi.es.piscinapp.core.platform.application.InactiveResourceException;
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
class CrewServiceIT {

    @Autowired
    private CrewService
            crewService;

    @Autowired
    private CrewRepository
            crewRepository;

    @Autowired
    private CrewMembershipRepository
            crewMembershipRepository;

    @Autowired
    private EmployeeService
            employeeService;

    @Test
    void shouldCreateCrewWithTrimmedName() {

        Crew crew =
                crewService
                        .createCrew(
                                " Morning Crew "
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
    void shouldAddActiveEmployee() {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        crewService
                .addMember(
                        crew.getId(),
                        employee.getId()
                );

        assertThat(
                crewMembershipRepository
                        .existsByCrewIdAndEmployeeId(
                                crew.getId(),
                                employee.getId()
                        )
        )
                .isTrue();
    }

    @Test
    void shouldRejectDuplicateMembership() {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        crewService
                .addMember(
                        crew.getId(),
                        employee.getId()
                );

        UUID crewId =
                crew.getId();

        UUID employeeId =
                employee.getId();

        assertThatThrownBy(
                () ->
                        crewService
                                .addMember(
                                        crewId,
                                        employeeId
                                )
        )
                .isInstanceOf(
                        CrewMembershipConflictException.class
                );
    }

    @Test
    void shouldAllowEmployeeInDifferentCrews() {

        Crew firstCrew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Crew secondCrew =
                crewService
                        .createCrew(
                                "Evening Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        crewService
                .addMember(
                        firstCrew.getId(),
                        employee.getId()
                );

        crewService
                .addMember(
                        secondCrew.getId(),
                        employee.getId()
                );

        assertThat(
                crewMembershipRepository
                        .existsByCrewIdAndEmployeeId(
                                firstCrew.getId(),
                                employee.getId()
                        )
        )
                .isTrue();

        assertThat(
                crewMembershipRepository
                        .existsByCrewIdAndEmployeeId(
                                secondCrew.getId(),
                                employee.getId()
                        )
        )
                .isTrue();
    }

    @Test
    void shouldRejectInactiveEmployeeMembership() {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        employeeService
                .updateStatus(
                        employee.getId(),
                        false
                );

        UUID crewId =
                crew.getId();

        UUID employeeId =
                employee.getId();

        assertThatThrownBy(
                () ->
                        crewService
                                .addMember(
                                        crewId,
                                        employeeId
                                )
        )
                .isInstanceOf(
                        InactiveResourceException.class
                );
    }

    @Test
    void shouldRemoveCrewMember() {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        crewService
                .addMember(
                        crew.getId(),
                        employee.getId()
                );

        crewService
                .removeMember(
                        crew.getId(),
                        employee.getId()
                );

        assertThat(
                crewMembershipRepository
                        .existsByCrewIdAndEmployeeId(
                                crew.getId(),
                                employee.getId()
                        )
        )
                .isFalse();

        assertThat(
                employeeService
                        .getEmployee(
                                employee.getId()
                        )
                        .getId()
        )
                .isEqualTo(
                        employee.getId()
                );
    }

    @Test
    void shouldRejectRemovingMissingMember() {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        UUID employeeId =
                UUID.randomUUID();

        assertThatThrownBy(
                () ->
                        crewService
                                .removeMember(
                                        crew.getId(),
                                        employeeId
                                )
        )
                .isInstanceOf(
                        CrewMemberNotFoundException.class
                );
    }

    @Test
    void shouldAssignCrewMemberAsSupervisor() {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        crewService
                .addMember(
                        crew.getId(),
                        employee.getId()
                );

        crewService
                .assignSupervisor(
                        crew.getId(),
                        employee.getId()
                );

        assertThat(
                crewRepository
                        .findById(
                                crew.getId()
                        )
                        .orElseThrow()
                        .getSupervisorEmployeeId()
        )
                .isEqualTo(
                        employee.getId()
                );
    }

    @Test
    void shouldRejectSupervisorOutsideCrew() {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        UUID crewId =
                crew.getId();

        UUID employeeId =
                employee.getId();

        assertThatThrownBy(
                () ->
                        crewService
                                .assignSupervisor(
                                        crewId,
                                        employeeId
                                )
        )
                .isInstanceOf(
                        CrewSupervisorConflictException.class
                );
    }

    @Test
    void shouldRejectInactiveEmployeeAsSupervisor() {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        crewService
                .addMember(
                        crew.getId(),
                        employee.getId()
                );

        employeeService
                .updateStatus(
                        employee.getId(),
                        false
                );

        UUID crewId =
                crew.getId();

        UUID employeeId =
                employee.getId();

        assertThatThrownBy(
                () ->
                        crewService
                                .assignSupervisor(
                                        crewId,
                                        employeeId
                                )
        )
                .isInstanceOf(
                        InactiveResourceException.class
                );
    }

    @Test
    void shouldRejectRemovingCurrentSupervisor() {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        crewService
                .addMember(
                        crew.getId(),
                        employee.getId()
                );

        crewService
                .assignSupervisor(
                        crew.getId(),
                        employee.getId()
                );

        UUID crewId =
                crew.getId();

        UUID employeeId =
                employee.getId();

        assertThatThrownBy(
                () ->
                        crewService
                                .removeMember(
                                        crewId,
                                        employeeId
                                )
        )
                .isInstanceOf(
                        CrewSupervisorConflictException.class
                );
    }

    @Test
    void shouldPreserveFormerSupervisorMembershipWhenReplaced() {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee first =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        Employee second =
                employeeService
                        .createEmployee(
                                "Luis",
                                "Garcia"
                        );

        crewService
                .addMember(
                        crew.getId(),
                        first.getId()
                );

        crewService
                .addMember(
                        crew.getId(),
                        second.getId()
                );

        crewService
                .assignSupervisor(
                        crew.getId(),
                        first.getId()
                );

        crewService
                .assignSupervisor(
                        crew.getId(),
                        second.getId()
                );

        assertThat(
                crewMembershipRepository
                        .existsByCrewIdAndEmployeeId(
                                crew.getId(),
                                first.getId()
                        )
        )
                .isTrue();

        assertThat(
                crewRepository
                        .findById(
                                crew.getId()
                        )
                        .orElseThrow()
                        .getSupervisorEmployeeId()
        )
                .isEqualTo(
                        second.getId()
                );
    }

    @Test
    void shouldClearSupervisorWithoutRemovingMembership() {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        crewService
                .addMember(
                        crew.getId(),
                        employee.getId()
                );

        crewService
                .assignSupervisor(
                        crew.getId(),
                        employee.getId()
                );

        crewService
                .clearSupervisor(
                        crew.getId()
                );

        assertThat(
                crewRepository
                        .findById(
                                crew.getId()
                        )
                        .orElseThrow()
                        .getSupervisorEmployeeId()
        )
                .isNull();

        assertThat(
                crewMembershipRepository
                        .existsByCrewIdAndEmployeeId(
                                crew.getId(),
                                employee.getId()
                        )
        )
                .isTrue();
    }

    @Test
    void shouldPreserveCrewStateWhenEmployeeBecomesInactive() {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        crewService
                .addMember(
                        crew.getId(),
                        employee.getId()
                );

        crewService
                .assignSupervisor(
                        crew.getId(),
                        employee.getId()
                );

        employeeService
                .updateStatus(
                        employee.getId(),
                        false
                );

        Crew storedCrew =
                crewRepository
                        .findById(
                                crew.getId()
                        )
                        .orElseThrow();

        assertThat(
                storedCrew
                        .getSupervisorEmployeeId()
        )
                .isEqualTo(
                        employee.getId()
                );

        assertThat(
                crewMembershipRepository
                        .existsByCrewIdAndEmployeeId(
                                crew.getId(),
                                employee.getId()
                        )
        )
                .isTrue();
    }

    @Test
    void shouldPreserveMembersAndSupervisorWhenCrewIsDeactivated() {

        Crew crew =
                crewService
                        .createCrew(
                                "Morning Crew"
                        );

        Employee employee =
                employeeService
                        .createEmployee(
                                "Ana",
                                "Martinez"
                        );

        crewService
                .addMember(
                        crew.getId(),
                        employee.getId()
                );

        crewService
                .assignSupervisor(
                        crew.getId(),
                        employee.getId()
                );

        crewService
                .updateStatus(
                        crew.getId(),
                        false
                );

        Crew storedCrew =
                crewRepository
                        .findById(
                                crew.getId()
                        )
                        .orElseThrow();

        assertThat(
                storedCrew.isActive()
        )
                .isFalse();

        assertThat(
                storedCrew
                        .getSupervisorEmployeeId()
        )
                .isEqualTo(
                        employee.getId()
                );

        assertThat(
                crewMembershipRepository
                        .existsByCrewIdAndEmployeeId(
                                crew.getId(),
                                employee.getId()
                        )
        )
                .isTrue();
    }
}
