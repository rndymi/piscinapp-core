package com.rndymi.es.piscinapp.core.execution.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(
        access = AccessLevel.PROTECTED
)
@Entity
@Table(
        name = "visit_observations"
)
public class VisitObservation {

    public static final int TEXT_MAX_LENGTH = 2000;

    @Id
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID id;

    @Column(
            name = "visit_id",
            nullable = false,
            updatable = false
    )
    private UUID visitId;

    @Column(
            nullable = false,
            length = TEXT_MAX_LENGTH,
            updatable = false
    )
    private String text;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(
            name = "created_by_account_id",
            nullable = false,
            updatable = false
    )
    private UUID createdByAccountId;

    @Column(
            name = "created_by_employee_id",
            nullable = false,
            updatable = false
    )
    private UUID createdByEmployeeId;

    public VisitObservation(
            UUID id,
            UUID visitId,
            String text,
            Instant createdAt,
            UUID createdByAccountId,
            UUID createdByEmployeeId
    ) {

        this.id =
                id;

        this.visitId =
                visitId;

        this.text =
                normalizeText(
                        text
                );

        this.createdAt =
                createdAt;

        this.createdByAccountId =
                createdByAccountId;

        this.createdByEmployeeId =
                createdByEmployeeId;
    }

    private String normalizeText(
            String value
    ) {

        if (value == null) {

            throw new IllegalArgumentException(
                    "Observation text is required"
            );
        }

        String normalized =
                value.strip();

        if (normalized.isEmpty()) {

            throw new IllegalArgumentException(
                    "Observation text must not be blank"
            );
        }

        if (
                normalized.length()
                        > TEXT_MAX_LENGTH
        ) {

            throw new IllegalArgumentException(
                    "Observation text must not exceed "
                            + TEXT_MAX_LENGTH
                            + " characters"
            );
        }

        return normalized;
    }
}
