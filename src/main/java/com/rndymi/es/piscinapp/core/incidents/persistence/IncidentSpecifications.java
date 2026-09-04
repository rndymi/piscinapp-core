package com.rndymi.es.piscinapp.core.incidents.persistence;

import com.rndymi.es.piscinapp.core.incidents.application.IncidentSearchCriteria;
import com.rndymi.es.piscinapp.core.incidents.domain.Incident;
import com.rndymi.es.piscinapp.core.incidents.domain.IncidentStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class IncidentSpecifications {

    private IncidentSpecifications() {
    }

    public static Specification<Incident> from(
            IncidentSearchCriteria criteria
    ) {

        return statusEquals(
                criteria.status()
        )
                .and(
                        visitEquals(
                                criteria.visitId()
                        )
                )
                .and(
                        createdByEmployeeEquals(
                                criteria.createdByEmployeeId()
                        )
                );
    }

    private static Specification<Incident> statusEquals(
            IncidentStatus status
    ) {

        if (status == null) {

            return Specification.unrestricted();
        }

        return (
                root,
                query,
                criteriaBuilder
        ) ->
                criteriaBuilder.equal(
                        root.get(
                                "status"
                        ),
                        status
                );
    }

    private static Specification<Incident> visitEquals(
            UUID visitId
    ) {

        if (visitId == null) {

            return Specification.unrestricted();
        }

        return (
                root,
                query,
                criteriaBuilder
        ) ->
                criteriaBuilder.equal(
                        root.get(
                                "visitId"
                        ),
                        visitId
                );
    }

    private static Specification<Incident>
    createdByEmployeeEquals(
            UUID employeeId
    ) {

        if (employeeId == null) {

            return Specification.unrestricted();
        }

        return (
                root,
                query,
                criteriaBuilder
        ) ->
                criteriaBuilder.equal(
                        root.get(
                                "createdByEmployeeId"
                        ),
                        employeeId
                );
    }
}
