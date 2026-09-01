package com.rndymi.es.piscinapp.core.planning.persistence;

import com.rndymi.es.piscinapp.core.planning.domain.VisitMaintenanceActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface VisitMaintenanceActivityRepository
        extends JpaRepository<VisitMaintenanceActivity, UUID> {

    List<VisitMaintenanceActivity> findAllByVisitId(
            UUID visitId
    );

    List<VisitMaintenanceActivity> findAllByVisitIdIn(
            Collection<UUID> visitIds
    );

    long deleteAllByVisitId(
            UUID visitId
    );
}
