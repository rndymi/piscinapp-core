package com.rndymi.es.piscinapp.core.employees.api;

import com.rndymi.es.piscinapp.core.platform.web.PageRequestFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EmployeePageRequestFactory {

    private static final Set<String>
            ALLOWED_SORT_FIELDS =
            Set.of(
                    "firstName",
                    "familyName",
                    "active"
            );

    private final PageRequestFactory
            pageRequestFactory;

    public EmployeePageRequestFactory(
            PageRequestFactory pageRequestFactory
    ) {

        this.pageRequestFactory =
                pageRequestFactory;
    }

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
                                Sort.Direction.ASC,
                                "familyName",
                                "firstName"
                        ),
                        "employee"
                );
    }
}
