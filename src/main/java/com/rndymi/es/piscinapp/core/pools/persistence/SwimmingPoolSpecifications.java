package com.rndymi.es.piscinapp.core.pools.persistence;

import com.rndymi.es.piscinapp.core.pools.domain.SwimmingPool;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class SwimmingPoolSpecifications {

    private SwimmingPoolSpecifications() {
    }

    public static Specification<SwimmingPool>
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

    public static Specification<SwimmingPool>
    nameOrAddressContains(
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

            var address =
                    criteriaBuilder.lower(
                            root.get(
                                    "address"
                            )
                    );

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            name,
                            pattern
                    ),
                    criteriaBuilder.like(
                            address,
                            pattern
                    )
            );
        };
    }
}
