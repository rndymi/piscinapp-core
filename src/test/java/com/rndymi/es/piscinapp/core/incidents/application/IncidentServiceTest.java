package com.rndymi.es.piscinapp.core.incidents.application;

import com.rndymi.es.piscinapp.core.crews.application.CrewLookup;
import com.rndymi.es.piscinapp.core.crews.application.CrewReference;
import com.rndymi.es.piscinapp.core.employees.application.EmployeeLookup;
import com.rndymi.es.piscinapp.core.employees.application.EmployeeReference;
import com.rndymi.es.piscinapp.core.execution.application.OperationalActor;
import com.rndymi.es.piscinapp.core.execution.application.OperationalActorResolver;
import com.rndymi.es.piscinapp.core.execution.application.exception.VisitExecutionForbiddenException;
import com.rndymi.es.piscinapp.core.identity.application.IdentityAccountLookup;
import com.rndymi.es.piscinapp.core.incidents.application.exception.IncidentResolutionForbiddenException;
import com.rndymi.es.piscinapp.core.incidents.application.exception.IncidentStateConflictException;
import com.rndymi.es.piscinapp.core.incidents.domain.Incident;
import com.rndymi.es.piscinapp.core.incidents.domain.IncidentStatus;
import com.rndymi.es.piscinapp.core.incidents.persistence.IncidentRepository;
import com.rndymi.es.piscinapp.core.planning.application.VisitExecutionOperations;
import com.rndymi.es.piscinapp.core.planning.application.VisitExecutionReference;
import com.rndymi.es.piscinapp.core.planning.application.exception.VisitStateConflictException;
import com.rndymi.es.piscinapp.core.planning.domain.VisitStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    private static final Instant NOW =
            Instant.parse(
                    "2026-09-04T01:00:00Z"
            );

    @Mock
    private IncidentRepository
            incidentRepository;

    @Mock
    private OperationalActorResolver
            operationalActorResolver;

    @Mock
    private IdentityAccountLookup
            identityAccountLookup;

    @Mock
    private EmployeeLookup
            employeeLookup;

    @Mock
    private CrewLookup
            crewLookup;

    @Mock
    private VisitExecutionOperations
            visitExecutionOperations;

    private IncidentService
            incidentService;

    @BeforeEach
    void setUp() {

        Clock clock =
                Clock.fixed(
                        NOW,
                        ZoneOffset.UTC
                );

        incidentService =
                new IncidentService(
                        incidentRepository,
                        operationalActorResolver,
                        identityAccountLookup,
                        employeeLookup,
                        crewLookup,
                        visitExecutionOperations,
                        clock
                );
    }

    @Test
    void shouldCreateOpenIncidentForAssignedCrewMember() {

        UUID visitId =
                UUID.randomUUID();

        UUID crewId =
                UUID.randomUUID();

        UUID accountId =
                UUID.randomUUID();

        UUID employeeId =
                UUID.randomUUID();

        when(
                operationalActorResolver
                        .resolve(
                                "worker"
                        )
        )
                .thenReturn(
                        new OperationalActor(
                                accountId,
                                employeeId
                        )
                );

        when(
                visitExecutionOperations
                        .requireExecutionVisit(
                                visitId
                        )
        )
                .thenReturn(
                        visit(
                                visitId,
                                crewId,
                                VisitStatus.IN_PROGRESS
                        )
                );

        when(
                crewLookup
                        .requireCrew(
                                crewId
                        )
        )
                .thenReturn(
                        crew(
                                crewId,
                                UUID.randomUUID(),
                                Set.of(
                                        employeeId
                                )
                        )
                );

        when(
                incidentRepository
                        .save(
                                any(
                                        Incident.class
                                )
                        )
        )
                .thenAnswer(
                        invocation ->
                                invocation
                                        .getArgument(
                                                0
                                        )
                );

        Incident incident =
                incidentService
                        .createIncident(
                                visitId,
                                "  Pump cannot start.  ",
                                "worker"
                        );

        assertThat(
                incident.getVisitId()
        )
                .isEqualTo(
                        visitId
                );

        assertThat(
                incident.getDescription()
        )
                .isEqualTo(
                        "Pump cannot start."
                );

        assertThat(
                incident.getStatus()
        )
                .isEqualTo(
                        IncidentStatus.OPEN
                );

        assertThat(
                incident.getCreatedAt()
        )
                .isEqualTo(
                        NOW
                );

        assertThat(
                incident.getCreatedByAccountId()
        )
                .isEqualTo(
                        accountId
                );

        assertThat(
                incident.getCreatedByEmployeeId()
        )
                .isEqualTo(
                        employeeId
                );

        verify(
                incidentRepository
        )
                .save(
                        incident
                );
    }

    @Test
    void shouldRejectIncidentCreationForUnassignedEmployee() {

        UUID visitId =
                UUID.randomUUID();

        UUID crewId =
                UUID.randomUUID();

        UUID employeeId =
                UUID.randomUUID();

        when(
                operationalActorResolver
                        .resolve(
                                "worker"
                        )
        )
                .thenReturn(
                        new OperationalActor(
                                UUID.randomUUID(),
                                employeeId
                        )
                );

        when(
                visitExecutionOperations
                        .requireExecutionVisit(
                                visitId
                        )
        )
                .thenReturn(
                        visit(
                                visitId,
                                crewId,
                                VisitStatus.IN_PROGRESS
                        )
                );

        when(
                crewLookup
                        .requireCrew(
                                crewId
                        )
        )
                .thenReturn(
                        crew(
                                crewId,
                                UUID.randomUUID(),
                                Set.of()
                        )
                );

        assertThatThrownBy(
                () ->
                        incidentService
                                .createIncident(
                                        visitId,
                                        "Pump cannot start.",
                                        "worker"
                                )
        )
                .isInstanceOf(
                        VisitExecutionForbiddenException.class
                );
    }

    @Test
    void shouldRejectIncidentCreationWhenVisitIsNotInProgress() {

        UUID visitId =
                UUID.randomUUID();

        UUID crewId =
                UUID.randomUUID();

        UUID employeeId =
                UUID.randomUUID();

        when(
                operationalActorResolver
                        .resolve(
                                "worker"
                        )
        )
                .thenReturn(
                        new OperationalActor(
                                UUID.randomUUID(),
                                employeeId
                        )
                );

        when(
                visitExecutionOperations
                        .requireExecutionVisit(
                                visitId
                        )
        )
                .thenReturn(
                        visit(
                                visitId,
                                crewId,
                                VisitStatus.PLANNED
                        )
                );

        when(
                crewLookup
                        .requireCrew(
                                crewId
                        )
        )
                .thenReturn(
                        crew(
                                crewId,
                                UUID.randomUUID(),
                                Set.of(
                                        employeeId
                                )
                        )
                );

        assertThatThrownBy(
                () ->
                        incidentService
                                .createIncident(
                                        visitId,
                                        "Pump cannot start.",
                                        "worker"
                                )
        )
                .isInstanceOf(
                        VisitStateConflictException.class
                );
    }

    @Test
    void shouldAllowAdminWithoutEmployeeToResolveIncident() {

        UUID visitId =
                UUID.randomUUID();

        UUID crewId =
                UUID.randomUUID();

        UUID accountId =
                UUID.randomUUID();

        Incident incident =
                openIncident(
                        visitId
                );

        when(
                identityAccountLookup
                        .requireAccountIdByPrincipalName(
                                "admin"
                        )
        )
                .thenReturn(
                        accountId
                );

        when(
                incidentRepository
                        .findById(
                                incident.getId()
                        )
        )
                .thenReturn(
                        Optional.of(
                                incident
                        )
                );

        when(
                visitExecutionOperations
                        .requireExecutionVisit(
                                visitId
                        )
        )
                .thenReturn(
                        visit(
                                visitId,
                                crewId,
                                VisitStatus.COMPLETED
                        )
                );

        when(
                employeeLookup
                        .findEmployeeByAccountId(
                                accountId
                        )
        )
                .thenReturn(
                        Optional.empty()
                );

        Incident result =
                incidentService
                        .resolveIncident(
                                incident.getId(),
                                "admin",
                                true
                        );

        assertThat(
                result.getStatus()
        )
                .isEqualTo(
                        IncidentStatus.RESOLVED
                );

        assertThat(
                result.getResolvedAt()
        )
                .isEqualTo(
                        NOW
                );

        assertThat(
                result.getResolvedByAccountId()
        )
                .isEqualTo(
                        accountId
                );

        assertThat(
                result.getResolvedByEmployeeId()
        )
                .isNull();
    }

    @Test
    void shouldAllowCurrentCrewSupervisorToResolveIncident() {

        UUID visitId =
                UUID.randomUUID();

        UUID crewId =
                UUID.randomUUID();

        UUID accountId =
                UUID.randomUUID();

        UUID supervisorEmployeeId =
                UUID.randomUUID();

        Incident incident =
                openIncident(
                        visitId
                );

        when(
                identityAccountLookup
                        .requireAccountIdByPrincipalName(
                                "supervisor"
                        )
        )
                .thenReturn(
                        accountId
                );

        when(
                incidentRepository
                        .findById(
                                incident.getId()
                        )
        )
                .thenReturn(
                        Optional.of(
                                incident
                        )
                );

        when(
                visitExecutionOperations
                        .requireExecutionVisit(
                                visitId
                        )
        )
                .thenReturn(
                        visit(
                                visitId,
                                crewId,
                                VisitStatus.IN_PROGRESS
                        )
                );

        when(
                employeeLookup
                        .findEmployeeByAccountId(
                                accountId
                        )
        )
                .thenReturn(
                        Optional.of(
                                new EmployeeReference(
                                        supervisorEmployeeId,
                                        true
                                )
                        )
                );

        when(
                crewLookup
                        .requireCrew(
                                crewId
                        )
        )
                .thenReturn(
                        crew(
                                crewId,
                                supervisorEmployeeId,
                                Set.of(
                                        supervisorEmployeeId
                                )
                        )
                );

        Incident result =
                incidentService
                        .resolveIncident(
                                incident.getId(),
                                "supervisor",
                                false
                        );

        assertThat(
                result.getStatus()
        )
                .isEqualTo(
                        IncidentStatus.RESOLVED
                );

        assertThat(
                result.getResolvedByAccountId()
        )
                .isEqualTo(
                        accountId
                );

        assertThat(
                result.getResolvedByEmployeeId()
        )
                .isEqualTo(
                        supervisorEmployeeId
                );
    }

    @Test
    void shouldRejectOrdinaryCrewMemberResolution() {

        UUID visitId =
                UUID.randomUUID();

        UUID crewId =
                UUID.randomUUID();

        UUID accountId =
                UUID.randomUUID();

        UUID employeeId =
                UUID.randomUUID();

        UUID supervisorEmployeeId =
                UUID.randomUUID();

        Incident incident =
                openIncident(
                        visitId
                );

        when(
                identityAccountLookup
                        .requireAccountIdByPrincipalName(
                                "worker"
                        )
        )
                .thenReturn(
                        accountId
                );

        when(
                incidentRepository
                        .findById(
                                incident.getId()
                        )
        )
                .thenReturn(
                        Optional.of(
                                incident
                        )
                );

        when(
                visitExecutionOperations
                        .requireExecutionVisit(
                                visitId
                        )
        )
                .thenReturn(
                        visit(
                                visitId,
                                crewId,
                                VisitStatus.IN_PROGRESS
                        )
                );

        when(
                employeeLookup
                        .findEmployeeByAccountId(
                                accountId
                        )
        )
                .thenReturn(
                        Optional.of(
                                new EmployeeReference(
                                        employeeId,
                                        true
                                )
                        )
                );

        when(
                crewLookup
                        .requireCrew(
                                crewId
                        )
        )
                .thenReturn(
                        crew(
                                crewId,
                                supervisorEmployeeId,
                                Set.of(
                                        supervisorEmployeeId,
                                        employeeId
                                )
                        )
                );

        assertThatThrownBy(
                () ->
                        incidentService
                                .resolveIncident(
                                        incident.getId(),
                                        "worker",
                                        false
                                )
        )
                .isInstanceOf(
                        IncidentResolutionForbiddenException.class
                );
    }

    @Test
    void shouldRejectDuplicateResolution() {

        UUID visitId =
                UUID.randomUUID();

        UUID crewId =
                UUID.randomUUID();

        UUID accountId =
                UUID.randomUUID();

        Incident incident =
                openIncident(
                        visitId
                );

        incident.resolve(
                NOW.minusSeconds(
                        60
                ),
                UUID.randomUUID(),
                null
        );

        when(
                identityAccountLookup
                        .requireAccountIdByPrincipalName(
                                "admin"
                        )
        )
                .thenReturn(
                        accountId
                );

        when(
                incidentRepository
                        .findById(
                                incident.getId()
                        )
        )
                .thenReturn(
                        Optional.of(
                                incident
                        )
                );

        when(
                visitExecutionOperations
                        .requireExecutionVisit(
                                visitId
                        )
        )
                .thenReturn(
                        visit(
                                visitId,
                                crewId,
                                VisitStatus.COMPLETED
                        )
                );

        when(
                employeeLookup
                        .findEmployeeByAccountId(
                                accountId
                        )
        )
                .thenReturn(
                        Optional.empty()
                );

        assertThatThrownBy(
                () ->
                        incidentService
                                .resolveIncident(
                                        incident.getId(),
                                        "admin",
                                        true
                                )
        )
                .isInstanceOf(
                        IncidentStateConflictException.class
                );

        assertThat(
                incident.getResolvedAt()
        )
                .isEqualTo(
                        NOW.minusSeconds(
                                60
                        )
                );
    }

    private Incident openIncident(
            UUID visitId
    ) {

        return new Incident(
                UUID.randomUUID(),
                visitId,
                "Pump cannot start.",
                NOW.minusSeconds(
                        300
                ),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    private VisitExecutionReference visit(
            UUID visitId,
            UUID crewId,
            VisitStatus status
    ) {

        return new VisitExecutionReference(
                visitId,
                UUID.randomUUID(),
                crewId,
                LocalDate.of(
                        2026,
                        9,
                        4
                ),
                LocalTime.of(
                        9,
                        0
                ),
                status,
                null,
                status
                        == VisitStatus.PLANNED
                        ?
                        null
                        :
                        NOW.minusSeconds(
                                600
                        ),
                null,
                null,
                status
                        == VisitStatus.COMPLETED
                        ?
                        NOW.minusSeconds(
                                60
                        )
                        :
                        null,
                null,
                null,
                List.of()
        );
    }

    private CrewReference crew(
            UUID crewId,
            UUID supervisorEmployeeId,
            Set<UUID> memberIds
    ) {

        return new CrewReference(
                crewId,
                true,
                supervisorEmployeeId,
                memberIds
        );
    }
}
