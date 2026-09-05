package com.rndymi.es.piscinapp.core.incidents.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
        name = "incidents"
)
public class Incident {

    public static final int DESCRIPTION_MAX_LENGTH = 2000;

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
            length = DESCRIPTION_MAX_LENGTH,
            updatable = false
    )
    private String description;

    @Enumerated(
            EnumType.STRING
    )
    @Column(
            nullable = false,
            length = 32
    )
    private IncidentStatus status;

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

    @Column(
            name = "resolved_at"
    )
    private Instant resolvedAt;

    @Column(
            name = "resolved_by_account_id"
    )
    private UUID resolvedByAccountId;

    @Column(
            name = "resolved_by_employee_id"
    )
    private UUID resolvedByEmployeeId;

    public Incident(
            UUID id,
            UUID visitId,
            String description,
            Instant createdAt,
            UUID createdByAccountId,
            UUID createdByEmployeeId
    ) {

        this.id =
                id;

        this.visitId =
                visitId;

        this.description =
                normalizeDescription(
                        description
                );

        this.status =
                IncidentStatus.OPEN;

        this.createdAt =
                createdAt;

        this.createdByAccountId =
                createdByAccountId;

        this.createdByEmployeeId =
                createdByEmployeeId;
    }

    public void resolve(
            Instant resolvedAt,
            UUID resolvedByAccountId,
            UUID resolvedByEmployeeId
    ) {

        if (
                status
                        != IncidentStatus.OPEN
        ) {

            throw new IllegalStateException(
                    "Incident must be OPEN"
            );
        }

        this.status =
                IncidentStatus.RESOLVED;

        this.resolvedAt =
                resolvedAt;

        this.resolvedByAccountId =
                resolvedByAccountId;

        this.resolvedByEmployeeId =
                resolvedByEmployeeId;
    }

    private String normalizeDescription(
            String value
    ) {

        if (value == null) {

            throw new IllegalArgumentException(
                    "Incident description is required"
            );
        }

        String normalized =
                value.strip();

        if (normalized.isEmpty()) {

            throw new IllegalArgumentException(
                    "Incident description must not be blank"
            );
        }

        if (
                normalized.length()
                        > DESCRIPTION_MAX_LENGTH
        ) {

            throw new IllegalArgumentException(
                    "Incident description must not exceed "
                            + DESCRIPTION_MAX_LENGTH
                            + " characters"
            );
        }

        return normalized;
    }
}
