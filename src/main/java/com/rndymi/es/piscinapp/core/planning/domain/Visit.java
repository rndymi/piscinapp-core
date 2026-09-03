package com.rndymi.es.piscinapp.core.planning.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(
        access = AccessLevel.PROTECTED
)
@Entity
@Table(
        name = "visits"
)
public class Visit {

    public static final int NOTES_MAX_LENGTH = 1000;

    @Id
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID id;

    @Column(
            name = "pool_id",
            nullable = false
    )
    private UUID poolId;

    @Column(
            name = "crew_id",
            nullable = false
    )
    private UUID crewId;

    @Column(
            name = "planned_date",
            nullable = false
    )
    private LocalDate plannedDate;

    @Column(
            name = "planned_time",
            nullable = false
    )
    private LocalTime plannedTime;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 32
    )
    private VisitStatus status;

    @Column(
            length = NOTES_MAX_LENGTH
    )
    private String notes;

    public Visit(
            UUID id,
            UUID poolId,
            UUID crewId,
            LocalDate plannedDate,
            LocalTime plannedTime,
            String notes
    ) {

        this.id =
                id;

        this.poolId =
                poolId;

        this.crewId =
                crewId;

        this.plannedDate =
                plannedDate;

        this.plannedTime =
                plannedTime;

        this.status =
                VisitStatus.PLANNED;

        this.notes =
                notes;
    }

    public void updatePlanning(
            UUID poolId,
            UUID crewId,
            LocalDate plannedDate,
            LocalTime plannedTime,
            String notes
    ) {

        requirePlanned();

        this.poolId =
                poolId;

        this.crewId =
                crewId;

        this.plannedDate =
                plannedDate;

        this.plannedTime =
                plannedTime;

        this.notes =
                notes;
    }

    public void cancel() {

        requirePlanned();

        this.status =
                VisitStatus.CANCELLED;
    }

    private void requirePlanned() {

        if (
                status
                        != VisitStatus.PLANNED
        ) {

            throw new IllegalStateException(
                    "Visit planning can only be changed while status is PLANNED"
            );
        }
    }
}
