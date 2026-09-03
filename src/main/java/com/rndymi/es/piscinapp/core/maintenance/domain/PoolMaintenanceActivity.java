package com.rndymi.es.piscinapp.core.maintenance.domain;

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
        name = "pool_maintenance_activities",
        uniqueConstraints =
        @UniqueConstraint(
                name =
                        "uk_pool_maintenance_activity",
                columnNames = {
                        "pool_id",
                        "maintenance_activity_id"
                }
        )
)
public class PoolMaintenanceActivity {

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
            name = "maintenance_activity_id",
            nullable = false
    )
    private UUID maintenanceActivityId;

    public PoolMaintenanceActivity(
            UUID id,
            UUID poolId,
            UUID maintenanceActivityId
    ) {

        this.id =
                id;

        this.poolId =
                poolId;

        this.maintenanceActivityId =
                maintenanceActivityId;
    }
}
