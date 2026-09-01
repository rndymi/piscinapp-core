package com.rndymi.es.piscinapp.core.platform.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

@Component
public class PageRequestFactory {

    public static final int DEFAULT_PAGE =
            0;

    public static final int DEFAULT_SIZE =
            20;

    public static final int MAX_SIZE =
            100;

    public Pageable create(
            int page,
            int size,
            String sort,
            Set<String> allowedSortFields,
            Sort defaultSort,
            String resourceName
    ) {

        validatePage(
                page,
                size
        );

        Sort requestedSort =
                parseSort(
                        sort,
                        allowedSortFields,
                        defaultSort,
                        resourceName
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
            String sort,
            Set<String> allowedSortFields,
            Sort defaultSort,
            String resourceName
    ) {

        if (
                sort == null
                        ||
                        sort.isBlank()
        ) {

            return defaultSort;
        }

        String[] parts =
                sort.split(
                        ",",
                        -1
                );

        if (parts.length > 2) {

            throw new IllegalArgumentException(
                    "Invalid "
                            + resourceName
                            + " sort"
            );
        }

        String field =
                parts[0].strip();

        if (
                !allowedSortFields
                        .contains(
                                field
                        )
        ) {

            throw new IllegalArgumentException(
                    "Unsupported "
                            + resourceName
                            + " sort field"
            );
        }

        Sort.Direction direction =
                Sort.Direction.ASC;

        if (parts.length == 2) {

            direction =
                    parseDirection(
                            parts[1],
                            resourceName
                    );
        }

        return Sort.by(
                direction,
                field
        );
    }

    private Sort.Direction parseDirection(
            String direction,
            String resourceName
    ) {

        return switch (
                direction
                        .strip()
                        .toLowerCase(
                                Locale.ROOT
                        )
                ) {

            case "asc" ->
                    Sort.Direction.ASC;

            case "desc" ->
                    Sort.Direction.DESC;

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported "
                                    + resourceName
                                    + " sort direction"
                    );
        };
    }
}
