package com.rndymi.es.piscinapp.core.incidents.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IncidentTest {

    @Test
    void shouldCreateOpenIncident() {

        UUID visitId =
                UUID.randomUUID();

        UUID accountId =
                UUID.randomUUID();

        UUID employeeId =
                UUID.randomUUID();

        Instant createdAt =
                Instant.parse(
                        "2026-09-04T00:00:00Z"
                );

        Incident incident =
                new Incident(
                        UUID.randomUUID(),
                        visitId,
                        "  Pump cannot start.  ",
                        createdAt,
                        accountId,
                        employeeId
                );

        assertThat(
                incident.getVisitId()
        )
                .isEqualTo(
                        visitId
                );

        assertThat(
                incident.getDescription()
        )
                .isEqualTo(
                        "Pump cannot start."
                );

        assertThat(
                incident.getStatus()
        )
                .isEqualTo(
                        IncidentStatus.OPEN
                );

        assertThat(
                incident.getCreatedAt()
        )
                .isEqualTo(
                        createdAt
                );

        assertThat(
                incident.getCreatedByAccountId()
        )
                .isEqualTo(
                        accountId
                );

        assertThat(
                incident.getCreatedByEmployeeId()
        )
                .isEqualTo(
                        employeeId
                );

        assertThat(
                incident.getResolvedAt()
        )
                .isNull();

        assertThat(
                incident.getResolvedByAccountId()
        )
                .isNull();

        assertThat(
                incident.getResolvedByEmployeeId()
        )
                .isNull();
    }

    @Test
    void shouldRejectBlankDescription() {

        assertThatThrownBy(
                () ->
                        new Incident(
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
    void shouldRejectDescriptionLongerThanLimit() {

        String description =
                "x".repeat(
                        Incident.DESCRIPTION_MAX_LENGTH
                                + 1
                );

        assertThatThrownBy(
                () ->
                        new Incident(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                description,
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
    void shouldResolveOpenIncident() {

        Incident incident =
                incident();

        Instant resolvedAt =
                Instant.parse(
                        "2026-09-04T01:00:00Z"
                );

        UUID accountId =
                UUID.randomUUID();

        UUID employeeId =
                UUID.randomUUID();

        incident.resolve(
                resolvedAt,
                accountId,
                employeeId
        );

        assertThat(
                incident.getStatus()
        )
                .isEqualTo(
                        IncidentStatus.RESOLVED
                );

        assertThat(
                incident.getResolvedAt()
        )
                .isEqualTo(
                        resolvedAt
                );

        assertThat(
                incident.getResolvedByAccountId()
        )
                .isEqualTo(
                        accountId
                );

        assertThat(
                incident.getResolvedByEmployeeId()
        )
                .isEqualTo(
                        employeeId
                );
    }

    @Test
    void shouldAllowResolutionWithoutEmployeeForAdminActor() {

        Incident incident =
                incident();

        UUID accountId =
                UUID.randomUUID();

        incident.resolve(
                Instant.parse(
                        "2026-09-04T01:00:00Z"
                ),
                accountId,
                null
        );

        assertThat(
                incident.getStatus()
        )
                .isEqualTo(
                        IncidentStatus.RESOLVED
                );

        assertThat(
                incident.getResolvedByAccountId()
        )
                .isEqualTo(
                        accountId
                );

        assertThat(
                incident.getResolvedByEmployeeId()
        )
                .isNull();
    }

    @Test
    void shouldRejectDuplicateResolution() {

        Incident incident =
                incident();

        incident.resolve(
                Instant.parse(
                        "2026-09-04T01:00:00Z"
                ),
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        assertThatThrownBy(
                () ->
                        incident.resolve(
                                Instant.parse(
                                        "2026-09-04T02:00:00Z"
                                ),
                                UUID.randomUUID(),
                                UUID.randomUUID()
                        )
        )
                .isInstanceOf(
                        IllegalStateException.class
                );
    }

    private Incident incident() {

        return new Incident(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Pump cannot start.",
                Instant.parse(
                        "2026-09-04T00:00:00Z"
                ),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }
}
