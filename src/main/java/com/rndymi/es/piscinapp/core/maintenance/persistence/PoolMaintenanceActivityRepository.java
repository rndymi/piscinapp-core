package com.rndymi.es.piscinapp.core.maintenance.persistence;

import com.rndymi.es.piscinapp.core.maintenance.domain.PoolMaintenanceActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PoolMaintenanceActivityRepository
        extends JpaRepository<PoolMaintenanceActivity, UUID> {

    boolean existsByPoolIdAndMaintenanceActivityId(
            UUID poolId,
            UUID maintenanceActivityId
    );

    long deleteByPoolIdAndMaintenanceActivityId(
            UUID poolId,
            UUID maintenanceActivityId
    );
}
