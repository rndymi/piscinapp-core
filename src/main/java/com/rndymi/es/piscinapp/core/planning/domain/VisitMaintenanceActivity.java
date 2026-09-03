package com.rndymi.es.piscinapp.core.planning.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
