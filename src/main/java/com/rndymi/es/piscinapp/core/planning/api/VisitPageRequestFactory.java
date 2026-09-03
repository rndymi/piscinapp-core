package com.rndymi.es.piscinapp.core.planning.api;

import com.rndymi.es.piscinapp.core.platform.web.PageRequestFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class VisitPageRequestFactory {

    private static final Set<String>
            ALLOWED_SORT_FIELDS =
            Set.of(
                    "plannedDate",
                    "plannedTime",
                    "status"
            );

    private final PageRequestFactory pageRequestFactory;

    public Pageable create(
            int page,
            int size,
            String sort
    ) {

        return pageRequestFactory
                .create(
                        page,
                        size,
                        sort,
                        ALLOWED_SORT_FIELDS,
                        Sort.by(
                                Sort.Order.asc(
                                        "plannedDate"
                                ),
                                Sort.Order.asc(
                                        "plannedTime"
                                )
                        ),
                        "visit"
                );
    }
}
