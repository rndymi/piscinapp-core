package com.rndymi.es.piscinapp.core.planning.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(
        access = AccessLevel.PROTECTED
)
@Entity
@Table(
        name = "visit_maintenance_activities",
        uniqueConstraints =
        @UniqueConstraint(
                name =
                        "uk_visit_maintenance_activity",
                columnNames = {
                        "visit_id",
                        "maintenance_activity_id"
                }
        )
)
public class VisitMaintenanceActivity {

    @Id
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID id;

    @Column(
            name = "visit_id",
            nullable = false
    )
    private UUID visitId;

    @Column(
            name = "maintenance_activity_id",
            nullable = false
    )
    private UUID maintenanceActivityId;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 32
    )
    private VisitActivityStatus status;

    @Column(
            name = "completed_at"
    )
    private Instant completedAt;

    @Column(
            name = "completed_by_account_id"
    )
    private UUID completedByAccountId;

    @Column(
            name = "completed_by_employee_id"
    )
    private UUID completedByEmployeeId;

    public VisitMaintenanceActivity(
            UUID id,
            UUID visitId,
            UUID maintenanceActivityId
    ) {

        this.id =
                id;

        this.visitId =
                visitId;

        this.maintenanceActivityId =
                maintenanceActivityId;
    }

    public void complete(
            Instant completedAt,
            UUID accountId,
            UUID employeeId
    ) {

        if (
                status
                        != VisitActivityStatus.PENDING
        ) {

            throw new IllegalStateException(
                    "Visit maintenance activity must be PENDING"
            );
        }

        this.status =
                VisitActivityStatus.COMPLETED;

        this.completedAt =
                completedAt;

        this.completedByAccountId =
                accountId;

        this.completedByEmployeeId =
                employeeId;
    }
}
