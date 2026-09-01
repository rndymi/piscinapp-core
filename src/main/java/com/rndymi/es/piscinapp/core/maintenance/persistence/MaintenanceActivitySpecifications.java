package com.rndymi.es.piscinapp.core.maintenance.persistence;

import com.rndymi.es.piscinapp.core.maintenance.domain.MaintenanceActivity;
import com.rndymi.es.piscinapp.core.maintenance.domain.PoolMaintenanceActivity;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;
import java.util.UUID;

public final class MaintenanceActivitySpecifications {

    private MaintenanceActivitySpecifications() {
    }

    public static Specification<MaintenanceActivity>
    activeEquals(
            Boolean active
    ) {

        if (active == null) {

            return Specification
                    .unrestricted();
        }

        return (
                root,
                query,
                criteriaBuilder
        ) ->
                criteriaBuilder.equal(
                        root.get(
                                "active"
                        ),
                        active
                );
    }

    public static Specification<MaintenanceActivity>
    nameOrDescriptionContains(
            String search
    ) {

        if (
                search == null
                        ||
                        search.isBlank()
        ) {

            return Specification
                    .unrestricted();
        }

        String pattern =
                "%"
                        + search
                        .strip()
                        .toLowerCase(
                                Locale.ROOT
                        )
                        + "%";

        return (
                root,
                query,
                criteriaBuilder
        ) -> {

            var name =
                    criteriaBuilder.lower(
                            root.get(
                                    "name"
                            )
                    );

            var description =
                    criteriaBuilder.lower(
                            root.get(
                                    "description"
                            )
                    );

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            name,
                            pattern
                    ),
                    criteriaBuilder.like(
                            description,
                            pattern
                    )
            );
        };
    }

    public static Specification<MaintenanceActivity>
    applicableToPool(
            UUID poolId
    ) {

        return (
                root,
                query,
                criteriaBuilder
        ) -> {

            var subquery =
                    query.subquery(
                            UUID.class
                    );

            var configuration =
                    subquery.from(
                            PoolMaintenanceActivity.class
                    );

            subquery.select(
                    configuration.get(
                            "maintenanceActivityId"
                    )
            );

            subquery.where(
                    criteriaBuilder.equal(
                            configuration.get(
                                    "poolId"
                            ),
                            poolId
                    )
            );

            return root.get(
                            "id"
                    )
                    .in(
                            subquery
                    );
        };
    }
}
