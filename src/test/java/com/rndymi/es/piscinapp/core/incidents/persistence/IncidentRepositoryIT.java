package com.rndymi.es.piscinapp.core.incidents.persistence;

import com.rndymi.es.piscinapp.core.incidents.application.IncidentSearchCriteria;
import com.rndymi.es.piscinapp.core.incidents.domain.Incident;
import com.rndymi.es.piscinapp.core.incidents.domain.IncidentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class IncidentRepositoryIT {

    @Autowired
    private IncidentRepository incidentRepository;

    @Test
    void shouldPersistOpenIncidentMetadata() {

        UUID visitId =
                UUID.randomUUID();

        UUID accountId =
                UUID.randomUUID();

        UUID employeeId =
                UUID.randomUUID();

        Instant createdAt =
                Instant.parse(
                        "2026-09-05T10:00:00Z"
                );

        Incident incident =
                incidentRepository
                        .saveAndFlush(
                                new Incident(
                                        UUID.randomUUID(),
                                        visitId,
                                        "Pump cannot start.",
                                        createdAt,
                                        accountId,
                                        employeeId
                                )
                        );

        Incident persisted =
                incidentRepository
                        .findById(
                                incident.getId()
                        )
                        .orElseThrow();

        assertThat(
                persisted.getVisitId()
        )
                .isEqualTo(
                        visitId
                );

        assertThat(
                persisted.getStatus()
        )
                .isEqualTo(
                        IncidentStatus.OPEN
                );

        assertThat(
                persisted.getCreatedAt()
        )
                .isEqualTo(
                        createdAt
                );

        assertThat(
                persisted.getCreatedByAccountId()
        )
                .isEqualTo(
                        accountId
                );

        assertThat(
                persisted.getCreatedByEmployeeId()
        )
                .isEqualTo(
                        employeeId
                );

        assertThat(
                persisted.getResolvedAt()
        )
                .isNull();
    }

    @Test
    void shouldPersistResolutionMetadata() {

        Incident incident =
                incidentRepository
                        .saveAndFlush(
                                incident(
                                        UUID.randomUUID(),
                                        UUID.randomUUID()
                                )
                        );

        Instant resolvedAt =
                Instant.parse(
                        "2026-09-05T11:00:00Z"
                );

        UUID resolverAccountId =
                UUID.randomUUID();

        incident.resolve(
                resolvedAt,
                resolverAccountId,
                null
        );

        incidentRepository.flush();

        Incident persisted =
                incidentRepository
                        .findById(
                                incident.getId()
                        )
                        .orElseThrow();

        assertThat(
                persisted.getStatus()
        )
                .isEqualTo(
                        IncidentStatus.RESOLVED
                );

        assertThat(
                persisted.getResolvedAt()
        )
                .isEqualTo(
                        resolvedAt
                );

        assertThat(
                persisted.getResolvedByAccountId()
        )
                .isEqualTo(
                        resolverAccountId
                );

        assertThat(
                persisted.getResolvedByEmployeeId()
        )
                .isNull();
    }

    @Test
    void shouldReturnVisitIncidentsInDeterministicOrder() {

        UUID visitId =
                UUID.randomUUID();

        Instant sameInstant =
                Instant.parse(
                        "2026-09-05T10:00:00Z"
                );

        UUID firstId =
                UUID.fromString(
                        "00000000-0000-0000-0000-000000000001"
                );

        UUID secondId =
                UUID.fromString(
                        "00000000-0000-0000-0000-000000000002"
                );

        incidentRepository.save(
                new Incident(
                        secondId,
                        visitId,
                        "Second",
                        sameInstant,
                        UUID.randomUUID(),
                        UUID.randomUUID()
                )
        );

        incidentRepository.save(
                new Incident(
                        firstId,
                        visitId,
                        "First",
                        sameInstant,
                        UUID.randomUUID(),
                        UUID.randomUUID()
                )
        );

        incidentRepository.flush();

        assertThat(
                incidentRepository
                        .findAllByVisitIdOrderByCreatedAtAscIdAsc(
                                visitId
                        )
        )
                .extracting(
                        Incident::getId
                )
                .containsExactly(
                        firstId,
                        secondId
                );
    }

    @Test
    void shouldFilterByStatusVisitAndCreator() {

        UUID expectedVisitId =
                UUID.randomUUID();

        UUID expectedEmployeeId =
                UUID.randomUUID();

        Incident open =
                incidentRepository.save(
                        new Incident(
                                UUID.randomUUID(),
                                expectedVisitId,
                                "Open incident",
                                Instant.parse(
                                        "2026-09-05T10:00:00Z"
                                ),
                                UUID.randomUUID(),
                                expectedEmployeeId
                        )
                );

        Incident resolved =
                new Incident(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        "Resolved incident",
                        Instant.parse(
                                "2026-09-05T11:00:00Z"
                        ),
                        UUID.randomUUID(),
                        UUID.randomUUID()
                );

        resolved.resolve(
                Instant.parse(
                        "2026-09-05T12:00:00Z"
                ),
                UUID.randomUUID(),
                null
        );

        incidentRepository.save(
                resolved
        );

        assertThat(
                incidentRepository
                        .findAll(
                                IncidentSpecifications.from(
                                        new IncidentSearchCriteria(
                                                IncidentStatus.OPEN,
                                                expectedVisitId,
                                                expectedEmployeeId
                                        )
                                ),
                                PageRequest.of(
                                        0,
                                        20,
                                        Sort.by(
                                                Sort.Order.desc(
                                                        "createdAt"
                                                ),
                                                Sort.Order.asc(
                                                        "id"
                                                )
                                        )
                                )
                        )
                        .getContent()
        )
                .extracting(
                        Incident::getId
                )
                .containsExactly(
                        open.getId()
                );
    }

    private Incident incident(
            UUID visitId,
            UUID employeeId
    ) {

        return new Incident(
                UUID.randomUUID(),
                visitId,
                "Pump cannot start.",
                Instant.parse(
                        "2026-09-05T10:00:00Z"
                ),
                UUID.randomUUID(),
                employeeId
        );
    }
}
