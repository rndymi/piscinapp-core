package com.rndymi.es.piscinapp.core.maintenance.api;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class MaintenanceActivityPageRequestFactory {

    public static final int
            DEFAULT_PAGE =
            0;

    public static final int
            DEFAULT_SIZE =
            20;

    public static final int
            MAX_SIZE =
            100;

    private static final Set<String>
            ALLOWED_SORT_FIELDS =
            Set.of(
                    "name",
                    "active"
            );

    private static final Map<String, Sort.Direction>
            ALLOWED_DIRECTIONS =
            Map.of(
                    "asc",
                    Sort.Direction.ASC,
                    "desc",
                    Sort.Direction.DESC
            );

    public Pageable create(
            int page,
            int size,
            String sort
    ) {

        validatePage(
                page,
                size
        );

        Sort requestedSort =
                parseSort(
                        sort
                );

        return PageRequest.of(
                page,
                size,
                requestedSort.and(
                        Sort.by(
                                Sort.Direction.ASC,
                                "id"
                        )
                )
        );
    }

    private void validatePage(
            int page,
            int size
    ) {

        if (page < 0) {

            throw new IllegalArgumentException(
                    "Page must be zero or greater"
            );
        }

        if (
                size < 1
                        ||
                        size > MAX_SIZE
        ) {

            throw new IllegalArgumentException(
                    "Size must be between 1 and "
                            + MAX_SIZE
            );
        }
    }

    private Sort parseSort(
            String sort
    ) {

        if (
                sort == null
                        ||
                        sort.isBlank()
        ) {

            return Sort.by(
                    Sort.Direction.ASC,
                    "name"
            );
        }

        String[] parts =
                sort.split(
                        ",",
                        -1
                );

        if (parts.length > 2) {

            throw new IllegalArgumentException(
                    "Invalid maintenance activity sort"
            );
        }

        String field =
                parts[0].strip();

        if (
                !ALLOWED_SORT_FIELDS
                        .contains(
                                field
                        )
        ) {

            throw new IllegalArgumentException(
                    "Unsupported maintenance activity sort field"
            );
        }

        Sort.Direction direction =
                Sort.Direction.ASC;

        if (parts.length == 2) {

            direction =
                    ALLOWED_DIRECTIONS
                            .get(
                                    parts[1]
                                            .strip()
                                            .toLowerCase()
                            );

            if (direction == null) {

                throw new IllegalArgumentException(
                        "Unsupported maintenance activity sort direction"
                );
            }
        }

        return Sort.by(
                direction,
                field
        );
    }
}
