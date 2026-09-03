package com.rndymi.es.piscinapp.core.execution.application;

import com.rndymi.es.piscinapp.core.crews.application.CrewLookup;
import com.rndymi.es.piscinapp.core.crews.application.CrewReference;
import com.rndymi.es.piscinapp.core.execution.application.exception.VisitExecutionForbiddenException;
import com.rndymi.es.piscinapp.core.planning.application.VisitActivityExecutionReference;
import com.rndymi.es.piscinapp.core.planning.application.VisitExecutionOperations;
import com.rndymi.es.piscinapp.core.planning.application.VisitExecutionReference;
import com.rndymi.es.piscinapp.core.planning.domain.VisitStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitExecutionService {

    private final OperationalActorResolver operationalActorResolver;
    private final CrewLookup crewLookup;
    private final VisitExecutionOperations visitExecutionOperations;
    private final Clock clock;

    @Transactional(readOnly = true)
    public VisitExecutionReference getExecutionDetailForAssignedActor(
            UUID visitId,
            String principalName
    ) {

        OperationalActor actor =
                operationalActorResolver.resolve(
                        principalName
                );

        VisitExecutionReference visit =
                visitExecutionOperations
                        .requireExecutionVisit(
                                visitId
                        );

        requireAssignedActor(
                actor,
                visit
        );

        return visit;
    }

    @Transactional(readOnly = true)
    public VisitExecutionReference getExecutionDetailForAdmin(
            UUID visitId
    ) {

        return visitExecutionOperations
                .requireExecutionVisit(
                        visitId
                );
    }

    @Transactional
    public VisitExecutionReference startVisit(
            UUID visitId,
            String principalName
    ) {

        OperationalActor actor =
                operationalActorResolver.resolve(
                        principalName
                );

        VisitExecutionReference visit =
                visitExecutionOperations
                        .requireExecutionVisit(
                                visitId
                        );

        requireAssignedActor(
                actor,
                visit
        );

        return visitExecutionOperations
                .startVisit(
                        visitId,
                        clock.instant(),
                        actor.accountId(),
                        actor.employeeId()
                );
    }

    @Transactional
    public VisitActivityExecutionReference completeActivity(
            UUID visitId,
            UUID maintenanceActivityId,
            String principalName
    ) {

        OperationalActor actor =
                operationalActorResolver.resolve(
                        principalName
                );

        VisitExecutionReference visit =
                visitExecutionOperations
                        .requireExecutionVisit(
                                visitId
                        );

        requireAssignedActor(
                actor,
                visit
        );

        return visitExecutionOperations
                .completeActivity(
                        visitId,
                        maintenanceActivityId,
                        clock.instant(),
                        actor.accountId(),
                        actor.employeeId()
                );
    }

    @Transactional
    public VisitExecutionReference completeVisit(
            UUID visitId,
            String principalName
    ) {

        OperationalActor actor =
                operationalActorResolver.resolve(
                        principalName
                );

        VisitExecutionReference visit =
                visitExecutionOperations
                        .requireExecutionVisit(
                                visitId
                        );

        requireAssignedActor(
                actor,
                visit
        );

        return visitExecutionOperations
                .completeVisit(
                        visitId,
                        clock.instant(),
                        actor.accountId(),
                        actor.employeeId()
                );
    }

    @Transactional(readOnly = true)
    public Page<VisitExecutionReference> findAssignedVisits(
            String principalName,
            LocalDate date,
            LocalDate fromDate,
            LocalDate toDate,
            VisitStatus status,
            Pageable pageable
    ) {

        OperationalActor actor =
                operationalActorResolver.resolve(
                        principalName
                );

        Set<UUID> crewIds =
                crewLookup
                        .findCrewIdsByEmployeeId(
                                actor.employeeId()
                        );

        return visitExecutionOperations
                .findAssignedVisits(
                        crewIds,
                        date,
                        fromDate,
                        toDate,
                        status,
                        pageable
                );
    }

    private void requireAssignedActor(
            OperationalActor actor,
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
                                actor.employeeId()
                        )
        ) {

            throw new VisitExecutionForbiddenException();
        }
    }
}
