package com.rndymi.es.piscinapp.core.execution.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VisitObservationTest {

    @Test
    void shouldTrimObservationText() {

        VisitObservation observation =
                new VisitObservation(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "  Filter housing shows wear.  ",
                        Instant.parse(
                                "2026-09-04T00:00:00Z"
                        ),
                        UUID.randomUUID(),
                        UUID.randomUUID()
                );

        assertThat(
                observation.getText()
        )
                .isEqualTo(
                        "Filter housing shows wear."
                );
    }

    @Test
    void shouldRejectBlankObservation() {

        assertThatThrownBy(
                () ->
                        new VisitObservation(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                "   ",
                                Instant.parse(
                                        "2026-09-04T00:00:00Z"
                                ),
                                UUID.randomUUID(),
                                UUID.randomUUID()
                        )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                );
    }

    @Test
    void shouldRejectObservationLongerThanLimit() {

        String text =
                "x".repeat(
                        VisitObservation.TEXT_MAX_LENGTH
                                + 1
                );

        assertThatThrownBy(
                () ->
                        new VisitObservation(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                text,
                                Instant.parse(
                                        "2026-09-04T00:00:00Z"
                                ),
                                UUID.randomUUID(),
                                UUID.randomUUID()
                        )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                );
    }
}
