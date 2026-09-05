package com.rndymi.es.piscinapp.core.incidents.api;

import com.rndymi.es.piscinapp.core.platform.web.PageRequestFactory;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IncidentPageRequestFactoryTest {

    private final IncidentPageRequestFactory factory =
            new IncidentPageRequestFactory(
                    new PageRequestFactory()
            );

    @Test
    void shouldUseDeterministicDefaultSort() {

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
                                "createdAt"
                        )
        )
                .isNotNull();

        assertThat(
                pageable
                        .getSort()
                        .getOrderFor(
                                "createdAt"
                        )
                        .isDescending()
        )
                .isTrue();

        assertThat(
                pageable
                        .getSort()
                        .getOrderFor(
                                "id"
                        )
        )
                .isNotNull();

        assertThat(
                pageable
                        .getSort()
                        .getOrderFor(
                                "id"
                        )
                        .isAscending()
        )
                .isTrue();
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
    void shouldAllowCreatedAtSort() {

        Pageable pageable =
                factory.create(
                        0,
                        20,
                        "createdAt,asc"
                );

        assertThat(
                pageable
                        .getSort()
                        .getOrderFor(
                                "createdAt"
                        )
                        .isAscending()
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
                                "visitId,asc"
                        )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                );
    }

    @Test
    void shouldRejectInvalidDirection() {

        assertThatThrownBy(
                () ->
                        factory.create(
                                0,
                                20,
                                "status,sideways"
                        )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                );
    }
}
