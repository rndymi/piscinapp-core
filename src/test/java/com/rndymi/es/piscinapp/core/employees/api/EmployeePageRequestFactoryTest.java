package com.rndymi.es.piscinapp.core.employees.api;

import com.rndymi.es.piscinapp.core.platform.web.PageRequestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmployeePageRequestFactoryTest {

    private EmployeePageRequestFactory
            factory;

    @BeforeEach
    void setUp() {

        PageRequestFactory pageRequestFactory =
                new PageRequestFactory();

        factory =
                new EmployeePageRequestFactory(
                        pageRequestFactory
                );
    }

    @Test
    void shouldCreateDefaultEmployeeSort() {

        Pageable pageable =
                factory.create(
                        0,
                        20,
                        null
                );

        assertThat(
                pageable.getPageNumber()
        )
                .isZero();

        assertThat(
                pageable.getPageSize()
        )
                .isEqualTo(
                        20
                );

        assertThat(
                pageable.getSort()
                        .getOrderFor(
                                "familyName"
                        )
        )
                .extracting(
                        Sort.Order::getDirection
                )
                .isEqualTo(
                        Sort.Direction.ASC
                );
    }

    @Test
    void shouldAcceptSupportedSort() {

        Pageable pageable =
                factory.create(
                        0,
                        20,
                        "firstName,desc"
                );

        assertThat(
                pageable.getSort()
                        .getOrderFor(
                                "firstName"
                        )
        )
                .extracting(
                        Sort.Order::getDirection
                )
                .isEqualTo(
                        Sort.Direction.DESC
                );
    }

    @Test
    void shouldRejectUnsupportedSortField() {

        assertThatThrownBy(
                () ->
                        factory.create(
                                0,
                                20,
                                "userAccountId,asc"
                        )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                );
    }

    @Test
    void shouldRejectPageBelowZero() {

        assertThatThrownBy(
                () ->
                        factory.create(
                                -1,
                                20,
                                null
                        )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                );
    }

    @Test
    void shouldRejectSizeAboveMaximum() {

        assertThatThrownBy(
                () ->
                        factory.create(
                                0,
                                101,
                                null
                        )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                );
    }

    @Test
    void shouldRejectZeroSize() {

        assertThatThrownBy(
                () ->
                        factory.create(
                                0,
                                0,
                                null
                        )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                );
    }
}
