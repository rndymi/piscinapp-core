package com.rndymi.es.piscinapp.core.pools.api;

import com.rndymi.es.piscinapp.core.platform.web.PageRequestFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SwimmingPoolPageRequestFactory {

    private static final Set<String>
            ALLOWED_SORT_FIELDS =
            Set.of(
                    "name",
                    "address",
                    "active"
            );

    private final PageRequestFactory
            pageRequestFactory;

    public SwimmingPoolPageRequestFactory(
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
                                "name"
                        ),
                        "swimming pool"
                );
    }
}
