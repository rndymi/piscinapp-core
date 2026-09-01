package com.rndymi.es.piscinapp.core.employees.persistence;

import com.rndymi.es.piscinapp.core.employees.domain.Employee;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class EmployeeSpecifications {

    private EmployeeSpecifications() {
    }

    public static Specification<Employee> activeEquals(
            Boolean active
    ) {

        if (active == null) {

            return Specification.unrestricted();
        }

        return (
                root,
                query,
                criteriaBuilder
        ) ->
                criteriaBuilder.equal(
                        root.get("active"),
                        active
                );
    }

    public static Specification<Employee> nameContains(
            String search
    ) {

        if (
                search == null
                        ||
                        search.isBlank()
        ) {

            return Specification.unrestricted();
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

            var firstName =
                    criteriaBuilder.lower(
                            root.get(
                                    "firstName"
                            )
                    );

            var familyName =
                    criteriaBuilder.lower(
                            root.get(
                                    "familyName"
                            )
                    );

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            firstName,
                            pattern
                    ),
                    criteriaBuilder.like(
                            familyName,
                            pattern
                    )
            );
        };
    }
}
