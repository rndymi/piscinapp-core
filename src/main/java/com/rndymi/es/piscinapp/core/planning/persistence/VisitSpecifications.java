package com.rndymi.es.piscinapp.core.planning.persistence;

import com.rndymi.es.piscinapp.core.planning.application.VisitSearchCriteria;
import com.rndymi.es.piscinapp.core.planning.domain.Visit;
import com.rndymi.es.piscinapp.core.planning.domain.VisitStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public final class VisitSpecifications {

    private VisitSpecifications() {
    }

    public static Specification<Visit> from(
            VisitSearchCriteria criteria
    ) {

        return dateEquals(
                criteria.date()
        )
                .and(
                        dateFrom(
                                criteria.fromDate()
                        )
                )
                .and(
                        dateTo(
                                criteria.toDate()
                        )
                )
                .and(
                        statusEquals(
                                criteria.status()
                        )
                )
                .and(
                        poolEquals(
                                criteria.poolId()
                        )
                )
                .and(
                        crewEquals(
                                criteria.crewId()
                        )
                );
    }

    public static Specification<Visit> crewIn(
            Set<UUID> crewIds
    ) {

        if (
                crewIds == null
                        ||
                        crewIds.isEmpty()
        ) {

            return (
                    root,
                    query,
                    criteriaBuilder
            ) ->
                    criteriaBuilder.disjunction();
        }

        return (
                root,
                query,
                criteriaBuilder
        ) ->
                root.get(
                                "crewId"
                        )
                        .in(
                                crewIds
                        );
    }

    private static Specification<Visit> dateEquals(
            LocalDate date
    ) {

        if (date == null) {
            return Specification.unrestricted();
        }

        return (
                root,
                query,
                criteriaBuilder
        ) ->
                criteriaBuilder.equal(
                        root.<LocalDate>get(
                                "plannedDate"
                        ),
                        date
                );
    }

    private static Specification<Visit> dateFrom(
            LocalDate fromDate
    ) {

        if (fromDate == null) {
            return Specification.unrestricted();
        }

        return (
                root,
                query,
                criteriaBuilder
        ) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.<LocalDate>get(
                                "plannedDate"
                        ),
                        fromDate
                );
    }

    private static Specification<Visit> dateTo(
            LocalDate toDate
    ) {

        if (toDate == null) {
            return Specification.unrestricted();
        }

        return (
                root,
                query,
                criteriaBuilder
        ) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.<LocalDate>get(
                                "plannedDate"
                        ),
                        toDate
                );
    }

    private static Specification<Visit> statusEquals(
            VisitStatus status
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

    private static Specification<Visit> poolEquals(
            UUID poolId
    ) {

        if (poolId == null) {
            return Specification.unrestricted();
        }

        return (
                root,
                query,
                criteriaBuilder
        ) ->
                criteriaBuilder.equal(
                        root.get(
                                "poolId"
                        ),
                        poolId
                );
    }

    private static Specification<Visit> crewEquals(
            UUID crewId
    ) {

        if (crewId == null) {
            return Specification.unrestricted();
        }

        return (
                root,
                query,
                criteriaBuilder
        ) ->
                criteriaBuilder.equal(
                        root.get(
                                "crewId"
                        ),
                        crewId
                );
    }
}
