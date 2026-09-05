package com.rndymi.es.piscinapp.core.incidents.application;

import com.rndymi.es.piscinapp.core.crews.application.CrewLookup;
import com.rndymi.es.piscinapp.core.crews.application.CrewReference;
import com.rndymi.es.piscinapp.core.employees.application.EmployeeLookup;
import com.rndymi.es.piscinapp.core.employees.application.EmployeeReference;
import com.rndymi.es.piscinapp.core.execution.application.OperationalActor;
import com.rndymi.es.piscinapp.core.execution.application.OperationalActorResolver;
import com.rndymi.es.piscinapp.core.execution.application.exception.VisitExecutionForbiddenException;
import com.rndymi.es.piscinapp.core.identity.application.IdentityAccountLookup;
import com.rndymi.es.piscinapp.core.incidents.application.exception.IncidentNotFoundException;
import com.rndymi.es.piscinapp.core.incidents.application.exception.IncidentResolutionForbiddenException;
import com.rndymi.es.piscinapp.core.incidents.application.exception.IncidentStateConflictException;
import com.rndymi.es.piscinapp.core.incidents.domain.Incident;
import com.rndymi.es.piscinapp.core.incidents.persistence.IncidentRepository;
import com.rndymi.es.piscinapp.core.planning.application.VisitExecutionOperations;
import com.rndymi.es.piscinapp.core.planning.application.VisitExecutionReference;
import com.rndymi.es.piscinapp.core.planning.application.exception.VisitStateConflictException;
import com.rndymi.es.piscinapp.core.planning.domain.VisitStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;

    private final OperationalActorResolver
            operationalActorResolver;

    private final IdentityAccountLookup
            identityAccountLookup;

    private final EmployeeLookup
            employeeLookup;

    private final CrewLookup
            crewLookup;

    private final VisitExecutionOperations
            visitExecutionOperations;

    private final Clock clock;

    @Transactional
    public Incident createIncident(
            UUID visitId,
            String description,
            String principalName
    ) {

        OperationalActor actor =
                operationalActorResolver
                        .resolve(
                                principalName
                        );

        VisitExecutionReference visit =
                visitExecutionOperations
                        .requireExecutionVisit(
                                visitId
                        );

        requireAssignedCrewMember(
                actor.employeeId(),
                visit
        );

        requireVisitInProgress(
                visit
        );

        Incident incident =
                new Incident(
                        UUID.randomUUID(),
                        visit.id(),
                        description,
                        clock.instant(),
                        actor.accountId(),
                        actor.employeeId()
                );

        return incidentRepository
                .save(
                        incident
                );
    }

    @Transactional
    public Incident resolveIncident(
            UUID incidentId,
            String principalName,
            boolean admin
    ) {

        UUID accountId =
                identityAccountLookup
                        .requireAccountIdByPrincipalName(
                                principalName
                        );

        Incident incident =
                requireIncident(
                        incidentId
                );

        VisitExecutionReference visit =
                visitExecutionOperations
                        .requireExecutionVisit(
                                incident.getVisitId()
                        );

        requireResolvableVisitState(
                incident,
                visit
        );

        UUID employeeId =
                admin
                        ?
                        resolveOptionalEmployeeId(
                                accountId
                        )
                        :
                        resolveSupervisorEmployeeId(
                                accountId,
                                visit
                        );

        resolve(
                incident,
                accountId,
                employeeId
        );

        return incident;
    }

    private Incident requireIncident(
            UUID incidentId
    ) {

        return incidentRepository
                .findById(
                        incidentId
                )
                .orElseThrow(
                        () ->
                                new IncidentNotFoundException(
                                        incidentId
                                )
                );
    }

    private void requireAssignedCrewMember(
            UUID employeeId,
            VisitExecutionReference visit
    ) {

        CrewReference crew =
                crewLookup
                        .requireCrew(
                                visit.crewId()
                        );

        if (
                !crew.memberIds()
                        .contains(
                                employeeId
                        )
        ) {

            throw new VisitExecutionForbiddenException();
        }
    }

    private void requireVisitInProgress(
            VisitExecutionReference visit
    ) {

        if (
                visit.status()
                        != VisitStatus.IN_PROGRESS
        ) {

            throw new VisitStateConflictException(
                    visit.id(),
                    visit.status()
            );
        }
    }

    private void requireResolvableVisitState(
            Incident incident,
            VisitExecutionReference visit
    ) {

        if (
                visit.status()
                        != VisitStatus.IN_PROGRESS
                        &&
                        visit.status()
                                != VisitStatus.COMPLETED
        ) {

            throw new IncidentStateConflictException(
                    incident.getId(),
                    "visit status is "
                            + visit.status()
            );
        }
    }

    private UUID resolveOptionalEmployeeId(
            UUID accountId
    ) {

        return employeeLookup
                .findEmployeeByAccountId(
                        accountId
                )
                .map(
                        EmployeeReference::id
                )
                .orElse(
                        null
                );
    }

    private UUID resolveSupervisorEmployeeId(
            UUID accountId,
            VisitExecutionReference visit
    ) {

        EmployeeReference employee =
                employeeLookup
                        .findEmployeeByAccountId(
                                accountId
                        )
                        .filter(
                                EmployeeReference::active
                        )
                        .orElseThrow(
                                IncidentResolutionForbiddenException::new
                        );

        CrewReference crew =
                crewLookup
                        .requireCrew(
                                visit.crewId()
                        );

        if (
                !employee.id()
                        .equals(
                                crew.supervisorEmployeeId()
                        )
        ) {

            throw new IncidentResolutionForbiddenException();
        }

        return employee.id();
    }

    private void resolve(
            Incident incident,
            UUID accountId,
            UUID employeeId
    ) {

        try {

            incident.resolve(
                    clock.instant(),
                    accountId,
                    employeeId
            );

        } catch (
                IllegalStateException exception
        ) {

            throw new IncidentStateConflictException(
                    incident.getId(),
                    "incident is already resolved"
            );
        }
    }
}
