package com.rndymi.es.piscinapp.core.crews.persistence;

import com.rndymi.es.piscinapp.core.crews.domain.Crew;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class CrewSpecifications {

    private CrewSpecifications() {
    }

    public static Specification<Crew>
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

    public static Specification<Crew>
    nameContains(
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
        ) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(
                                root.get(
                                        "name"
                                )
                        ),
                        pattern
                );
    }
}
