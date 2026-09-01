package com.rndymi.es.piscinapp.core.maintenance.persistence;

import com.rndymi.es.piscinapp.core.maintenance.domain.MaintenanceActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface MaintenanceActivityRepository
        extends JpaRepository<MaintenanceActivity, UUID>,
        JpaSpecificationExecutor<MaintenanceActivity> {
}
