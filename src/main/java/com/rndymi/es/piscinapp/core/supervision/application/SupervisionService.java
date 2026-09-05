package com.rndymi.es.piscinapp.core.supervision.application;

import com.rndymi.es.piscinapp.core.crews.application.CrewLookup;
import com.rndymi.es.piscinapp.core.crews.application.CrewReference;
import com.rndymi.es.piscinapp.core.execution.application.OperationalActor;
import com.rndymi.es.piscinapp.core.execution.application.OperationalActorResolver;
import com.rndymi.es.piscinapp.core.execution.application.VisitObservationLookup;
import com.rndymi.es.piscinapp.core.incidents.application.IncidentLookup;
import com.rndymi.es.piscinapp.core.planning.application.VisitSupervisionLookup;
import com.rndymi.es.piscinapp.core.planning.application.VisitExecutionReference;
import com.rndymi.es.piscinapp.core.supervision.application.exception.VisitSupervisionForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupervisionService {

    private final VisitSupervisionLookup visitSupervisionLookup;
    private final CrewLookup crewLookup;
    private final OperationalActorResolver operationalActorResolver;
    private final VisitObservationLookup visitObservationLookup;
    private final IncidentLookup incidentLookup;

    @Transactional(readOnly = true)
    public VisitSupervisionReference getVisitSupervision(
            UUID visitId,
            String principalName,
            boolean admin
    ) {

        VisitExecutionReference visit =
                visitSupervisionLookup
                        .requireVisitForSupervision(
                                visitId
                        );

        if (!admin) {

            requireCurrentCrewSupervisor(
                    visit,
                    principalName
            );
        }

        return new VisitSupervisionReference(
                visit,
                visitObservationLookup
                        .findObservationsByVisitId(
                                visitId
                        ),
                incidentLookup
                        .findIncidentsByVisitId(
                                visitId
                        )
        );
    }

    private void requireCurrentCrewSupervisor(
            VisitExecutionReference visit,
            String principalName
    ) {

        OperationalActor actor =
                operationalActorResolver
                        .resolve(
                                principalName
                        );

        CrewReference crew =
                crewLookup
                        .requireCrew(
                                visit.crewId()
                        );

        if (
                !actor.employeeId()
                        .equals(
                                crew.supervisorEmployeeId()
                        )
        ) {

            throw new VisitSupervisionForbiddenException();
        }
    }
}
