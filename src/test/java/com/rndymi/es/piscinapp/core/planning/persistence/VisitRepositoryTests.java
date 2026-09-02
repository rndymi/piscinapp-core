package com.rndymi.es.piscinapp.core.planning.persistence;

import com.rndymi.es.piscinapp.core.planning.application.VisitSearchCriteria;
import com.rndymi.es.piscinapp.core.planning.domain.Visit;
import com.rndymi.es.piscinapp.core.planning.domain.VisitMaintenanceActivity;
import com.rndymi.es.piscinapp.core.planning.domain.VisitStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class VisitRepositoryTests {

    @Autowired
    private VisitRepository
            visitRepository;

    @Autowired
    private VisitMaintenanceActivityRepository
            activityRepository;

    @Test
    void shouldPersistVisitAndSelectedActivities() {

        Visit visit =
                visitRepository
                        .saveAndFlush(
                                visit(
                                        LocalDate.of(
                                                2026,
                                                9,
                                                3
                                        ),
                                        LocalTime.of(
                                                9,
                                                30
                                        )
                                )
                        );

        UUID activityId =
                UUID.randomUUID();

        activityRepository
                .saveAndFlush(
                        new VisitMaintenanceActivity(
                                UUID.randomUUID(),
                                visit.getId(),
                                activityId
                        )
                );

        Visit persisted =
                visitRepository
                        .findById(
                                visit.getId()
                        )
                        .orElseThrow();

        assertThat(
                persisted.getStatus()
        )
                .isEqualTo(
                        VisitStatus.PLANNED
                );

        assertThat(
                persisted.getPlannedDate()
        )
                .isEqualTo(
                        LocalDate.of(
                                2026,
                                9,
                                3
                        )
                );

        assertThat(
                activityRepository
                        .findAllByVisitId(
                                visit.getId()
                        )
        )
                .extracting(
                        VisitMaintenanceActivity
                                ::getMaintenanceActivityId
                )
                .containsExactly(
                        activityId
                );
    }

    @Test
    void shouldEnforceUniqueSelectedActivity() {

        Visit visit =
                visitRepository
                        .saveAndFlush(
                                visit(
                                        LocalDate.of(
                                                2026,
                                                9,
                                                3
                                        ),
                                        LocalTime.of(
                                                9,
                                                30
                                        )
                                )
                        );

        UUID activityId =
                UUID.randomUUID();

        activityRepository
                .saveAndFlush(
                        new VisitMaintenanceActivity(
                                UUID.randomUUID(),
                                visit.getId(),
                                activityId
                        )
                );

        assertThatThrownBy(
                () ->
                        activityRepository
                                .saveAndFlush(
                                        new VisitMaintenanceActivity(
                                                UUID.randomUUID(),
                                                visit.getId(),
                                                activityId
                                        )
                                )
        )
                .isInstanceOf(
                        RuntimeException.class
                );
    }

    @Test
    void shouldFilterByInclusiveDateRange() {

        visitRepository.save(
                visit(
                        LocalDate.of(
                                2026,
                                9,
                                1
                        ),
                        LocalTime.of(
                                8,
                                0
                        )
                )
        );

        visitRepository.save(
                visit(
                        LocalDate.of(
                                2026,
                                9,
                                3
                        ),
                        LocalTime.of(
                                8,
                                0
                        )
                )
        );

        visitRepository.save(
                visit(
                        LocalDate.of(
                                2026,
                                9,
                                8
                        ),
                        LocalTime.of(
                                8,
                                0
                        )
                )
        );

        assertThat(
                visitRepository
                        .findAll(
                                VisitSpecifications.from(
                                        new VisitSearchCriteria(
                                                null,
                                                LocalDate.of(
                                                        2026,
                                                        9,
                                                        1
                                                ),
                                                LocalDate.of(
                                                        2026,
                                                        9,
                                                        7
                                                ),
                                                null,
                                                null,
                                                null
                                        )
                                ),
                                PageRequest.of(
                                        0,
                                        20,
                                        Sort.by(
                                                "plannedDate"
                                        )
                                )
                        )
                        .getContent()
        )
                .hasSize(
                        2
                );
    }

    private Visit visit(
            LocalDate date,
            LocalTime time
    ) {

        return new Visit(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                date,
                time,
                "Planning notes"
        );
    }
}
