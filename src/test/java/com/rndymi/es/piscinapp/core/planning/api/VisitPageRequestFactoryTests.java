package com.rndymi.es.piscinapp.core.planning.api;

import com.rndymi.es.piscinapp.core.platform.web.PageRequestFactory;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VisitPageRequestFactoryTests {

    private final VisitPageRequestFactory
            factory =
            new VisitPageRequestFactory(
                    new PageRequestFactory()
            );

    @Test
    void shouldUseOperationalDefaultSort() {

        Pageable pageable =
                factory.create(
                        0,
                        20,
                        null
                );

        assertThat(
                pageable
                        .getSort()
                        .getOrderFor(
                                "plannedDate"
                        )
        )
                .isNotNull();

        assertThat(
                pageable
                        .getSort()
                        .getOrderFor(
                                "plannedTime"
                        )
        )
                .isNotNull();

        assertThat(
                pageable
                        .getSort()
                        .getOrderFor(
                                "id"
                        )
        )
                .isNotNull();
    }

    @Test
    void shouldAllowStatusSort() {

        Pageable pageable =
                factory.create(
                        0,
                        20,
                        "status,desc"
                );

        assertThat(
                pageable
                        .getSort()
                        .getOrderFor(
                                "status"
                        )
                        .isDescending()
        )
                .isTrue();
    }

    @Test
    void shouldRejectUnsupportedSort() {

        assertThatThrownBy(
                () ->
                        factory.create(
                                0,
                                20,
                                "crewId,asc"
                        )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                );
    }
}
